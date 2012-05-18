package org.apache.camel.osgi.integration;

import org.apache.camel.*;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.Constants;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class OsgiRoundRobinEndpointConsumerErrorHandlingTest extends OsgiIntegrationTest {

    @Inject
    @Filter(value = "(camel.context.symbolicname=org.apache.camel.osgi.integration.OsgiRoundRobinEndpointConsumerErrorHandlingTest.producer)")
    private CamelContext producerContext;

    @Inject
    @Filter("(camel.context.symbolicname=org.apache.camel.osgi.integration.OsgiRoundRobinEndpointConsumerErrorHandlingTest.consumer)")
    private CamelContext consumerContext;

    @Configuration
    public Option[] config() {
        return new Option[] {
            defaultOptions(),

            provision(
                bundle()
                    .add("OSGI-INF/blueprint/camel-context.xml", getClass().getResource(getClass().getSimpleName() + "-consumer.xml"))
                    .set(Constants.BUNDLE_NAME, getClass().getName() + ".consumer")
                    .set(Constants.BUNDLE_SYMBOLICNAME, getClass().getName() + ".consumer")
                    .set(Constants.BUNDLE_VERSION, "1.0.0")
                    .removeHeader(Constants.IMPORT_PACKAGE)
                    .removeHeader(Constants.EXPORT_PACKAGE)
                    .build(),
                bundle()
                    .add("OSGI-INF/blueprint/camel-context.xml", getClass().getResource(getClass().getSimpleName() + "-producer.xml"))
                    .set(Constants.BUNDLE_NAME, getClass().getName() + ".producer")
                    .set(Constants.BUNDLE_SYMBOLICNAME, getClass().getName() + ".producer")
                    .set(Constants.BUNDLE_VERSION, "1.0.0")
                    .removeHeader(Constants.IMPORT_PACKAGE)
                    .removeHeader(Constants.EXPORT_PACKAGE)
                    .build()
            ),
        };
    }

    @Test
    public void testHandleException() throws Exception {
        final int endpointCount = 5;
        final int failedEndpoint = 2;

        MockEndpoint finish = consumerContext.getEndpoint("mock:finish" + failedEndpoint, MockEndpoint.class);
        finish.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                throw new RuntimeException("TestException!!!");
            }
        });
        finish.expectedMessageCount(4);
        
        for(int i = 0; i < endpointCount; i++) {
            finish = consumerContext.getEndpoint("mock:finish" + i, MockEndpoint.class);
            if(i == failedEndpoint) {
                finish.allMessages().body().isEqualTo("1234567890");
            } else {
                finish.expectedBodiesReceived("1234567890");
            }
        }

        MockEndpoint consumerException = consumerContext.getEndpoint("mock:exception", MockEndpoint.class);
        consumerException.expectedBodiesReceived("1234567890");

        MockEndpoint producerException = producerContext.getEndpoint("mock:exception", MockEndpoint.class);
        producerException.expectedMessageCount(0);

        ProducerTemplate producerTemplate = producerContext.createProducerTemplate();
        for(int i = 0; i < endpointCount; i++) {
            if(i == failedEndpoint) {
                try {
                    producerTemplate.sendBody("direct:start", "1234567890");
                    fail("CamelExecutionException expected");
                } catch (CamelExecutionException e) {
                    assertEquals("TestException!!!", e.getCause().getMessage());
                }
            } else {
                producerTemplate.sendBody("direct:start", "1234567890");
            }
        }

        MockEndpoint.assertIsSatisfied(consumerContext);
        MockEndpoint.assertIsSatisfied(producerContext);
    }
}

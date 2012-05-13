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

import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class OsgiMulticastEndpointConsumerErrorHandlingTest extends OsgiIntegrationTest {

    @Inject
    @Filter(value = "(camel.context.symbolicname=org.apache.camel.osgi.integration.OsgiMulticastEndpointConsumerErrorHandlingTest.producer)")
    private CamelContext producerContext;

    @Inject
    @Filter("(camel.context.symbolicname=org.apache.camel.osgi.integration.OsgiMulticastEndpointConsumerErrorHandlingTest.consumer1)")
    private CamelContext consumer1Context;

    @Inject
    @Filter("(camel.context.symbolicname=org.apache.camel.osgi.integration.OsgiMulticastEndpointConsumerErrorHandlingTest.consumer2)")
    private CamelContext consumer2Context;

    @Configuration
    public Option[] config() {
        return new Option[] {
            defaultOptions(),

            provision(
                bundle()
                    .add("OSGI-INF/blueprint/camel-context.xml", getClass().getResource(getClass().getSimpleName() + "-consumer1.xml"))
                    .set(Constants.BUNDLE_NAME, getClass().getName() + ".consumer1")
                    .set(Constants.BUNDLE_SYMBOLICNAME, getClass().getName() + ".consumer1")
                    .set(Constants.BUNDLE_VERSION, "1.0.0")
                    .removeHeader(Constants.IMPORT_PACKAGE)
                    .removeHeader(Constants.EXPORT_PACKAGE)
                    .build(),
                bundle()
                    .add("OSGI-INF/blueprint/camel-context.xml", getClass().getResource(getClass().getSimpleName() + "-consumer2.xml"))
                    .set(Constants.BUNDLE_NAME, getClass().getName() + ".consumer2")
                    .set(Constants.BUNDLE_SYMBOLICNAME, getClass().getName() + ".consumer2")
                    .set(Constants.BUNDLE_VERSION, "1.0.0")
                    .removeHeader(Constants.IMPORT_PACKAGE)
                    .removeHeader(Constants.EXPORT_PACKAGE)
                    .build(),
                bundle()
                    .add("OSGI-INF/blueprint/camel-context.xml", getClass().getResource(getClass().getSimpleName() + "-producer.xml"))
                    .set(Constants.BUNDLE_NAME, getClass().getName() + ".producer")
                    .set(Constants.BUNDLE_SYMBOLICNAME, getClass().getName() + ".producer")
                    .set(Constants.BUNDLE_VERSION, "1.0.0")
                    .set(Constants.IMPORT_PACKAGE, "org.apache.camel.processor.aggregate")
                    .removeHeader(Constants.EXPORT_PACKAGE)
                    .build()
            ),
        };
    }

    @Test
    public void sendMessage() throws Exception {
        MockEndpoint consumer1 = consumer1Context.getEndpoint("mock:finish", MockEndpoint.class);
        consumer1.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                in.setBody(in.getBody() + "-1");
            }
        });
        consumer1.expectedBodiesReceived("1234567890");

        MockEndpoint consumer2 = consumer2Context.getEndpoint("mock:finish", MockEndpoint.class);
        consumer2.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                in.setBody(in.getBody() + "-2");
            }
        });
        consumer2.expectedBodiesReceived("1234567890");

        MockEndpoint producer = producerContext.getEndpoint("mock:finish", MockEndpoint.class);
        producer.expectedBodiesReceivedInAnyOrder("1234567890-1", "1234567890-2");

        ProducerTemplate producerTemplate = producerContext.createProducerTemplate();
        producerTemplate.sendBody("direct:start", "1234567890");

        MockEndpoint.assertIsSatisfied(producerContext);
        MockEndpoint.assertIsSatisfied(consumer1Context);
        MockEndpoint.assertIsSatisfied(consumer2Context);
    }

}

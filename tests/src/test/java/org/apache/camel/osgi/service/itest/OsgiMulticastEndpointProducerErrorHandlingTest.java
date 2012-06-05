/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.camel.osgi.service.itest;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy;
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
public class OsgiMulticastEndpointProducerErrorHandlingTest extends OsgiIntegrationTest {

    @Inject
    @Filter(value = "(camel.context.symbolicname=org.apache.camel.osgi.service.itest.OsgiMulticastEndpointProducerErrorHandlingTest.producer)")
    private CamelContext producerContext;

    @Inject
    @Filter("(camel.context.symbolicname=org.apache.camel.osgi.service.itest.OsgiMulticastEndpointProducerErrorHandlingTest.consumer1)")
    private CamelContext consumer1Context;

    @Inject
    @Filter("(camel.context.symbolicname=org.apache.camel.osgi.service.itest.OsgiMulticastEndpointProducerErrorHandlingTest.consumer2)")
    private CamelContext consumer2Context;

    @Configuration
    public Option[] config() {
        return new Option[] {
            defaultOptions(),

            provision(
                bundle()
                    .add("OSGI-INF/blueprint/camel-context.xml",
                        getClass().getResource(getClass().getSimpleName() + "-consumer1.xml"))
                    .set(Constants.BUNDLE_NAME, getClass().getName() + ".consumer1")
                    .set(Constants.BUNDLE_SYMBOLICNAME, getClass().getName() + ".consumer1")
                    .set(Constants.BUNDLE_VERSION, "1.0.0")
                    .removeHeader(Constants.IMPORT_PACKAGE)
                    .removeHeader(Constants.EXPORT_PACKAGE)
                    .build(),
                bundle()
                    .add("OSGI-INF/blueprint/camel-context.xml",
                        getClass().getResource(getClass().getSimpleName() + "-consumer2.xml"))
                    .set(Constants.BUNDLE_NAME, getClass().getName() + ".consumer2")
                    .set(Constants.BUNDLE_SYMBOLICNAME, getClass().getName() + ".consumer2")
                    .set(Constants.BUNDLE_VERSION, "1.0.0")
                    .removeHeader(Constants.IMPORT_PACKAGE)
                    .removeHeader(Constants.EXPORT_PACKAGE)
                    .build(),
                bundle()
                    .add("OSGI-INF/blueprint/camel-context.xml",
                        getClass().getResource(getClass().getSimpleName() + "-producer.xml"))
                    .set(Constants.BUNDLE_NAME, getClass().getName() + ".producer")
                    .set(Constants.BUNDLE_SYMBOLICNAME, getClass().getName() + ".producer")
                    .set(Constants.BUNDLE_VERSION, "1.0.0")
                    .set(Constants.IMPORT_PACKAGE, GroupedExchangeAggregationStrategy.class.getPackage().getName())
                    .removeHeader(Constants.EXPORT_PACKAGE)
                    .build()
            ),
        };
    }

    @Test
    public void testHandleException() throws Exception {
        MockEndpoint consumer1 = consumer1Context.getEndpoint("mock:finish", MockEndpoint.class);
        consumer1.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                throw new RuntimeException("TestException-1");
            }
        });
        consumer1.expectedMessageCount(4);
        consumer1.allMessages().body().isEqualTo("1234567890");

        MockEndpoint consumer2 = consumer2Context.getEndpoint("mock:finish", MockEndpoint.class);
        consumer2.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                in.setBody(in.getBody() + "-2");
            }
        });
        consumer2.expectedMessageCount(1);
        consumer2.expectedBodiesReceived("1234567890");

        MockEndpoint exception = producerContext.getEndpoint("mock:exception", MockEndpoint.class);
        exception.expectedMessageCount(1);
        exception.expectedBodiesReceived("1234567890");

        MockEndpoint finish = producerContext.getEndpoint("mock:finish", MockEndpoint.class);
        finish.expectedMessageCount(2);
        finish.expectedBodiesReceivedInAnyOrder("1234567890", "1234567890-2");

        ProducerTemplate producerTemplate = producerContext.createProducerTemplate();
        producerTemplate.sendBody("direct:start", "1234567890");

        MockEndpoint.assertIsSatisfied(producerContext);
        MockEndpoint.assertIsSatisfied(consumer1Context);
        MockEndpoint.assertIsSatisfied(consumer2Context);
    }

    @Test
    public void testHandleExceptionStopOnException() throws Exception {
        MockEndpoint consumer1 = consumer1Context.getEndpoint("mock:finish", MockEndpoint.class);
        consumer1.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                throw new RuntimeException("TestException-1");
            }
        });
        consumer1.expectedMessageCount(4);
        consumer1.allMessages().body().isEqualTo("1234567890");

        MockEndpoint exception = producerContext.getEndpoint("mock:exception", MockEndpoint.class);
        exception.expectedMessageCount(1);
        exception.expectedBodiesReceived("1234567890");

        MockEndpoint finish = producerContext.getEndpoint("mock:finish", MockEndpoint.class);
        finish.expectedMessageCount(0);

        ProducerTemplate producerTemplate = producerContext.createProducerTemplate();
        try {
            producerTemplate.sendBody("direct:startStopOnException", "1234567890");
            fail("CamelExecutionException expected");
        } catch (CamelExecutionException e) {
            Throwable cause = e.getCause();
            while (cause.getClass() != RuntimeException.class) {
                cause = cause.getCause();
            }
            assertEquals("TestException-1", cause.getMessage());
        }

        MockEndpoint.assertIsSatisfied(producerContext);
        MockEndpoint.assertIsSatisfied(consumer1Context);
        MockEndpoint.assertIsSatisfied(consumer2Context);
    }

    @Test
    public void testHandleExceptionParallel() throws Exception {
        MockEndpoint consumer1 = consumer1Context.getEndpoint("mock:finish", MockEndpoint.class);
        consumer1.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                throw new RuntimeException("TestException-1");
            }
        });
        consumer1.expectedMessageCount(4);
        consumer1.allMessages().body().isEqualTo("1234567890");

        MockEndpoint consumer2 = consumer2Context.getEndpoint("mock:finish", MockEndpoint.class);
        consumer2.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                in.setBody(in.getBody() + "-2");
            }
        });
        consumer2.expectedMessageCount(1);
        consumer2.expectedBodiesReceived("1234567890");

        MockEndpoint exception = producerContext.getEndpoint("mock:exception", MockEndpoint.class);
        exception.expectedMessageCount(1);
        exception.expectedBodiesReceived("1234567890");

        MockEndpoint finish = producerContext.getEndpoint("mock:finish", MockEndpoint.class);
        finish.expectedMessageCount(0);

        ProducerTemplate producerTemplate = producerContext.createProducerTemplate();
        try {
            producerTemplate.sendBody("direct:startStopOnExceptionParallel", "1234567890");
            fail("CamelExecutionException expected");
        } catch (CamelExecutionException e) {
            Throwable cause = e.getCause();
            while (cause.getClass() != RuntimeException.class) {
                cause = cause.getCause();
            }
            assertEquals("TestException-1", cause.getMessage());
        }

        MockEndpoint.assertIsSatisfied(producerContext);
        MockEndpoint.assertIsSatisfied(consumer1Context);
        MockEndpoint.assertIsSatisfied(consumer2Context);
    }

}

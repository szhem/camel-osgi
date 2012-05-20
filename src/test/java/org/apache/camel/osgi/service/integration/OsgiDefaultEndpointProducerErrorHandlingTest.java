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

package org.apache.camel.osgi.service.integration;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
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
public class OsgiDefaultEndpointProducerErrorHandlingTest extends OsgiIntegrationTest {

    @Inject
    @Filter(value = "(camel.context.symbolicname=org.apache.camel.osgi.service.integration.OsgiDefaultEndpointProducerErrorHandlingTest.producer)")
    private CamelContext producerContext;

    @Inject
    @Filter("(camel.context.symbolicname=org.apache.camel.osgi.service.integration.OsgiDefaultEndpointProducerErrorHandlingTest.consumer)")
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
    public void testSendMessage() throws Exception {
        MockEndpoint finish = consumerContext.getEndpoint("mock:finish", MockEndpoint.class);
        finish.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                throw new RuntimeException("TestException!!!");
            }
        });
        finish.expectedMessageCount(4);

        MockEndpoint consumerException = consumerContext.getEndpoint("mock:exception", MockEndpoint.class);
        consumerException.expectedMessageCount(0);

        MockEndpoint producerException = producerContext.getEndpoint("mock:exception", MockEndpoint.class);
        producerException.expectedBodiesReceived("1234567890");

        ProducerTemplate producerTemplate = producerContext.createProducerTemplate();
        try {
            producerTemplate.sendBody("direct:start", "1234567890");
            fail("CamelExecutionException expected");
        } catch (CamelExecutionException e) {
            assertEquals("TestException!!!", e.getCause().getMessage());
        }

        MockEndpoint.assertIsSatisfied(consumerContext);
        MockEndpoint.assertIsSatisfied(producerContext);
    }
}

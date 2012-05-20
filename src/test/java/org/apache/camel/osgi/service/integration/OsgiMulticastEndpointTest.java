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
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

import javax.inject.Inject;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class OsgiMulticastEndpointTest extends OsgiIntegrationTest {

    @Inject
    @Filter(value = "(camel.context.symbolicname=org.apache.camel.osgi.service.integration.OsgiMulticastEndpointTest.producer)")
    private CamelContext producerContext;

    @Inject
    @Filter("(camel.context.symbolicname=org.apache.camel.osgi.service.integration.OsgiMulticastEndpointTest.consumer1)")
    private CamelContext consumer1Context;

    @Inject
    @Filter("(camel.context.symbolicname=org.apache.camel.osgi.service.integration.OsgiMulticastEndpointTest.consumer2)")
    private CamelContext consumer2Context;

    @Configuration
    public Option[] config() {
        return new Option[] {
            defaultOptions(),

            mavenBundle("org.ops4j.pax.tinybundles", "tinybundles").versionAsInProject(),

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
    public void testSendMessage() throws Exception {
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

    @Test
    public void testRestartService() throws Exception {
        MockEndpoint consumer1 = consumer1Context.getEndpoint("mock:finish", MockEndpoint.class);
        consumer1.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                in.setBody(in.getBody() + "-1");
            }
        });
        consumer1.expectedBodiesReceivedInAnyOrder("1234567890-1", "1234567890-3");

        MockEndpoint consumer2 = consumer2Context.getEndpoint("mock:finish", MockEndpoint.class);
        consumer2.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                in.setBody(in.getBody() + "-2");
            }
        });
        consumer2.expectedBodiesReceivedInAnyOrder("1234567890-1", "1234567890-2", "1234567890-3");

        MockEndpoint producer = producerContext.getEndpoint("mock:finish", MockEndpoint.class);
        producer.expectedBodiesReceivedInAnyOrder(
                "1234567890-1-1", "1234567890-1-2", "1234567890-2-2", "1234567890-3-1", "1234567890-3-2");

        ProducerTemplate producerTemplate = producerContext.createProducerTemplate();
        producerTemplate.sendBody("direct:start", "1234567890-1");

        consumer1Context.stopRoute("consumer1");
        producerTemplate.sendBody("direct:start", "1234567890-2");

        consumer1Context.startRoute("consumer1");
        producerTemplate.sendBody("direct:start", "1234567890-3");

        MockEndpoint.assertIsSatisfied(producerContext);
        MockEndpoint.assertIsSatisfied(consumer1Context);
        MockEndpoint.assertIsSatisfied(consumer2Context);
    }

    @Test
    public void testInstallUninstallBundle() throws Exception {
        MockEndpoint consumer1 = consumer1Context.getEndpoint("mock:finish", MockEndpoint.class);
        consumer1.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                in.setBody(in.getBody() + "-1");
            }
        });
        consumer1.expectedBodiesReceivedInAnyOrder("1234567890-1", "1234567890-2", "1234567890-3");

        MockEndpoint consumer2 = consumer2Context.getEndpoint("mock:finish", MockEndpoint.class);
        consumer2.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                in.setBody(in.getBody() + "-2");
            }
        });
        consumer2.expectedBodiesReceivedInAnyOrder("1234567890-1", "1234567890-2", "1234567890-3");

        MockEndpoint producer = producerContext.getEndpoint("mock:finish", MockEndpoint.class);
        producer.expectedBodiesReceivedInAnyOrder(
                "1234567890-1-1", "1234567890-1-2",
                "1234567890-2-1", "1234567890-2-2", "1234567890-2-3",
                "1234567890-3-1", "1234567890-3-2");

        ProducerTemplate producerTemplate = producerContext.createProducerTemplate();
        producerTemplate.sendBody("direct:start", "1234567890-1");

        Bundle bundle = installBundle();
        bundle.start();

        CamelContext consumer3Context = getOsgiService(CamelContext.class, "(camel.context.symbolicname=" + getClass().getName() + ".consumer3)");
        MockEndpoint consumer3 = consumer3Context.getEndpoint("mock:finish", MockEndpoint.class);
        consumer3.expectedBodiesReceived("1234567890-2");
        consumer3.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                in.setBody(in.getBody() + "-3");
            }
        });
        producerTemplate.sendBody("direct:start", "1234567890-2");

        bundle.stop();
        producerTemplate.sendBody("direct:start", "1234567890-3");

        MockEndpoint.assertIsSatisfied(producerContext);
        MockEndpoint.assertIsSatisfied(consumer1Context);
        MockEndpoint.assertIsSatisfied(consumer2Context);
        MockEndpoint.assertIsSatisfied(consumer3Context);
    }

    private Bundle installBundle() throws BundleException {
        TinyBundle tinyBundle = bundle()
            .add("OSGI-INF/blueprint/camel-context.xml", getClass().getResource(getClass().getSimpleName() + "-consumer3.xml"))
            .set(Constants.BUNDLE_NAME, getClass().getName() + ".consumer3")
            .set(Constants.BUNDLE_SYMBOLICNAME, getClass().getName() + ".consumer3")
            .set(Constants.BUNDLE_VERSION, "1.0.0")
            .removeHeader(Constants.IMPORT_PACKAGE)
            .removeHeader(Constants.EXPORT_PACKAGE);

        return bundleContext.installBundle("location", tinyBundle.build());
    }
}

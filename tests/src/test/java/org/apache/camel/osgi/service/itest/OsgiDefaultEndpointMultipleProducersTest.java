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
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
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
public class OsgiDefaultEndpointMultipleProducersTest extends OsgiIntegrationTest {

    @Inject
    @Filter(value = "(camel.context.symbolicname=org.apache.camel.osgi.service.itest.OsgiDefaultEndpointMultipleProducersTest.producer1)")
    private CamelContext producer1Context;

    @Inject
    @Filter(value = "(camel.context.symbolicname=org.apache.camel.osgi.service.itest.OsgiDefaultEndpointMultipleProducersTest.producer2)")
    private CamelContext producer2Context;

    @Inject
    @Filter("(camel.context.symbolicname=org.apache.camel.osgi.service.itest.OsgiDefaultEndpointMultipleProducersTest.consumer)")
    private CamelContext consumerContext;

    @Configuration
    public Option[] config() {
        return new Option[] {
            defaultOptions(),

            mavenBundle("org.ops4j.pax.tinybundles", "tinybundles").versionAsInProject(),

            provision(
                bundle()
                    .add("OSGI-INF/blueprint/camel-context.xml",
                        getClass().getResource(getClass().getSimpleName() + "-consumer.xml"))
                    .set(Constants.BUNDLE_NAME, getClass().getName() + ".consumer")
                    .set(Constants.BUNDLE_SYMBOLICNAME, getClass().getName() + ".consumer")
                    .set(Constants.BUNDLE_VERSION, "1.0.0")
                    .removeHeader(Constants.IMPORT_PACKAGE)
                    .removeHeader(Constants.EXPORT_PACKAGE)
                    .build(),
                bundle()
                    .add("OSGI-INF/blueprint/camel-context.xml",
                        getClass().getResource(getClass().getSimpleName() + "-producer1.xml"))
                    .set(Constants.BUNDLE_NAME, getClass().getName() + ".producer1")
                    .set(Constants.BUNDLE_SYMBOLICNAME, getClass().getName() + ".producer1")
                    .set(Constants.BUNDLE_VERSION, "1.0.0")
                    .removeHeader(Constants.IMPORT_PACKAGE)
                    .removeHeader(Constants.EXPORT_PACKAGE)
                    .build(),
                bundle()
                    .add("OSGI-INF/blueprint/camel-context.xml",
                        getClass().getResource(getClass().getSimpleName() + "-producer2.xml"))
                    .set(Constants.BUNDLE_NAME, getClass().getName() + ".producer2")
                    .set(Constants.BUNDLE_SYMBOLICNAME, getClass().getName() + ".producer2")
                    .set(Constants.BUNDLE_VERSION, "1.0.0")
                    .removeHeader(Constants.IMPORT_PACKAGE)
                    .removeHeader(Constants.EXPORT_PACKAGE)
                    .build()
            ),
        };
    }

    @Test
    public void testSendMessage() throws Exception {
        MockEndpoint consumerFinish = consumerContext.getEndpoint("mock:finish", MockEndpoint.class);
        consumerFinish.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                in.setBody(in.getBody() + "-reply");
            }
        });
        consumerFinish.expectedBodiesReceivedInAnyOrder("1234567890-1", "1234567890-2");

        MockEndpoint producer1Finish = producer1Context.getEndpoint("mock:finish", MockEndpoint.class);
        producer1Finish.expectedBodiesReceived("1234567890-1-reply");

        MockEndpoint producer2Finish = producer2Context.getEndpoint("mock:finish", MockEndpoint.class);
        producer2Finish.expectedBodiesReceived("1234567890-2-reply");

        producer1Context.createProducerTemplate().sendBody("direct:start", "1234567890-1");
        producer2Context.createProducerTemplate().sendBody("direct:start", "1234567890-2");

        MockEndpoint.assertIsSatisfied(consumerContext);
        MockEndpoint.assertIsSatisfied(producer1Context);
        MockEndpoint.assertIsSatisfied(producer2Context);
    }

    @Test
    public void testInstallBundle() throws Exception {
        MockEndpoint consumerFinish = consumerContext.getEndpoint("mock:finish", MockEndpoint.class);
        consumerFinish.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                in.setBody(in.getBody() + "-reply");
            }
        });
        consumerFinish.expectedBodiesReceivedInAnyOrder("1234567890-1", "1234567890-2", "1234567890-3");

        MockEndpoint producer1Finish = producer1Context.getEndpoint("mock:finish", MockEndpoint.class);
        producer1Finish.expectedBodiesReceived("1234567890-1-reply");

        MockEndpoint producer2Finish = producer2Context.getEndpoint("mock:finish", MockEndpoint.class);
        producer2Finish.expectedBodiesReceived("1234567890-2-reply");

        producer1Context.createProducerTemplate().sendBody("direct:start", "1234567890-1");
        producer2Context.createProducerTemplate().sendBody("direct:start", "1234567890-2");

        Bundle bundle = installBundle();
        bundle.start();

        CamelContext producer3Context = getOsgiService(CamelContext.class,
            "(camel.context.symbolicname=" + getClass().getName() + ".producer3)");
        MockEndpoint producer3Finish = producer3Context.getEndpoint("mock:finish", MockEndpoint.class);
        producer3Finish.expectedBodiesReceived("1234567890-3-reply");
        producer3Context.createProducerTemplate().sendBody("direct:start", "1234567890-3");

        bundle.uninstall();

        MockEndpoint.assertIsSatisfied(consumerContext);
        MockEndpoint.assertIsSatisfied(producer1Context);
        MockEndpoint.assertIsSatisfied(producer2Context);
        MockEndpoint.assertIsSatisfied(producer3Context);
    }

    private Bundle installBundle() throws BundleException {
        TinyBundle tinyBundle = bundle()
            .add("OSGI-INF/blueprint/camel-context.xml",
                getClass().getResource(getClass().getSimpleName() + "-producer3.xml"))
            .set(Constants.BUNDLE_NAME, getClass().getName() + ".producer3")
            .set(Constants.BUNDLE_SYMBOLICNAME, getClass().getName() + ".producer3")
            .set(Constants.BUNDLE_VERSION, "1.0.0")
            .removeHeader(Constants.IMPORT_PACKAGE)
            .removeHeader(Constants.EXPORT_PACKAGE);

        return bundleContext.installBundle("location", tinyBundle.build());
    }
}

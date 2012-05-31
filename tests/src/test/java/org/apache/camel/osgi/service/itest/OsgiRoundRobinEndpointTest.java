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
import java.util.ArrayList;
import java.util.List;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class OsgiRoundRobinEndpointTest extends OsgiIntegrationTest {

    @Inject
    @Filter(value = "(camel.context.symbolicname=org.apache.camel.osgi.service.itest.OsgiRoundRobinEndpointTest.producer)")
    private CamelContext producerContext;

    @Inject
    @Filter("(camel.context.symbolicname=org.apache.camel.osgi.service.itest.OsgiRoundRobinEndpointTest.consumer1)")
    private CamelContext consumer1Context;

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
        final int endpointCount = 10;
        ProducerTemplate producerTemplate = producerContext.createProducerTemplate();

        for(int i = 0; i < endpointCount; i++) {
            MockEndpoint finish = consumer1Context.getEndpoint("mock:finish" + i, MockEndpoint.class);
            finish.expectedBodiesReceived("1234567890");

            producerTemplate.sendBody("direct:start", "1234567890");
        }

        MockEndpoint.assertIsSatisfied(consumer1Context);
    }

    @Test
    public void testRestartService() throws Exception {
        final int endpointCount = 10;
        ProducerTemplate producerTemplate = producerContext.createProducerTemplate();

        for(int i = 0; i < endpointCount; i++) {
            List<String> expectedBodies = new ArrayList<String>();

            expectedBodies.add("1234567890-1");
            if(i % 2 != 0) {
                expectedBodies.add("1234567890-2");
                expectedBodies.add("1234567890-2");
            }
            expectedBodies.add("1234567890-3");

            MockEndpoint finish = consumer1Context.getEndpoint("mock:finish" + i, MockEndpoint.class);
            finish.expectedBodiesReceived(expectedBodies);
        }

        for(int i = 0; i < endpointCount; i++) {
            producerTemplate.sendBody("direct:start", "1234567890-1");
        }

        for(int i = 0; i < endpointCount; i += 2) {
            consumer1Context.stopRoute("route" + i);
        }

        for(int i = 0; i < endpointCount; i++) {
            producerTemplate.sendBody("direct:start", "1234567890-2");
        }

        for(int i = 0; i < endpointCount; i += 2) {
            consumer1Context.startRoute("route" + i);
        }

        for(int i = 0; i < endpointCount; i++) {
            producerTemplate.sendBody("direct:start", "1234567890-3");
        }

        MockEndpoint.assertIsSatisfied(consumer1Context);
    }

    @Test
    public void testInstallUninstallBundle() throws Exception {
        final int endpointCount = 10;
        ProducerTemplate producerTemplate = producerContext.createProducerTemplate();

        for(int i = 0; i < endpointCount; i++) {
            MockEndpoint finish = consumer1Context.getEndpoint("mock:finish" + i, MockEndpoint.class);
            finish.expectedBodiesReceived("1234567890-1", "1234567890-2", "1234567890-3");

            producerTemplate.sendBody("direct:start", "1234567890-1");
        }

        // add new bundle with service that has highest ranking, so it has to receive the next sent message
        Bundle bundle = installBundle();
        bundle.start();

        CamelContext consumer2Context = getOsgiService(CamelContext.class, "(camel.context.symbolicname=" + getClass().getName() + ".consumer2)");
        MockEndpoint finish100 = consumer2Context.getEndpoint("mock:finish100", MockEndpoint.class);
        finish100.expectedBodiesReceived("1234567890-2");

        for(int i = 0; i < endpointCount + 1; i++) {
            producerTemplate.sendBody("direct:start", "1234567890-2");
        }

        // remove new bundle
        bundle.uninstall();
        for(int i = 0; i < endpointCount; i++) {
            producerTemplate.sendBody("direct:start", "1234567890-3");
        }

        for(int i = 0; i < endpointCount; i++) {
            MockEndpoint finish = consumer1Context.getEndpoint("mock:finish" + i, MockEndpoint.class);
            System.out.println("finish" + i + ": " + finish.getExchanges());
        }
        System.out.println("finish100: " + finish100.getExchanges());

        MockEndpoint.assertIsSatisfied(consumer1Context);
        MockEndpoint.assertIsSatisfied(consumer2Context);
    }

    private Bundle installBundle() throws BundleException {
        TinyBundle tinyBundle = bundle()
            .add("OSGI-INF/blueprint/camel-context.xml", getClass().getResource(getClass().getSimpleName() + "-consumer2.xml"))
            .set(Constants.BUNDLE_NAME, getClass().getName() + ".consumer2")
            .set(Constants.BUNDLE_SYMBOLICNAME, getClass().getName() + ".consumer2")
            .set(Constants.BUNDLE_VERSION, "1.0.0")
            .removeHeader(Constants.IMPORT_PACKAGE)
            .removeHeader(Constants.EXPORT_PACKAGE);

        return bundleContext.installBundle("location", tinyBundle.build());
    }
}

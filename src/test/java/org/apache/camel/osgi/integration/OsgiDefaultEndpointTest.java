package org.apache.camel.osgi.integration;

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

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class OsgiDefaultEndpointTest extends OsgiIntegrationTest {

    @Inject
    @Filter(value = "(camel.context.symbolicname=org.apache.camel.osgi.integration.OsgiDefaultEndpointTest.producer)")
    private CamelContext producerContext;

    @Inject
    @Filter("(camel.context.symbolicname=org.apache.camel.osgi.integration.OsgiDefaultEndpointTest.consumer1)")
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
        for(int i = 0; i < 9; i++) {
            MockEndpoint finish = consumer1Context.getEndpoint("mock:finish" + i, MockEndpoint.class);
            finish.expectedMessageCount(0);
        }

        MockEndpoint finish9 = consumer1Context.getEndpoint("mock:finish9", MockEndpoint.class);
        finish9.expectedBodiesReceived("1234567890-1");

        ProducerTemplate producerTemplate = producerContext.createProducerTemplate();
        producerTemplate.sendBody("direct:start", "1234567890-1");

        MockEndpoint.assertIsSatisfied(consumer1Context);
    }

    @Test
    public void testRestartService() throws Exception {
        for(int i = 0; i < 8; i++) {
            MockEndpoint finish = consumer1Context.getEndpoint("mock:finish" + i, MockEndpoint.class);
            finish.expectedMessageCount(0);
        }

        MockEndpoint finish8 = consumer1Context.getEndpoint("mock:finish8", MockEndpoint.class);
        finish8.expectedBodiesReceived("1234567890-2");

        MockEndpoint finish9 = consumer1Context.getEndpoint("mock:finish9", MockEndpoint.class);
        finish9.expectedBodiesReceived("1234567890-1", "1234567890-3");

        ProducerTemplate producerTemplate = producerContext.createProducerTemplate();
        producerTemplate.sendBody("direct:start", "1234567890-1");

        // stop service, so finish8 will receive the message
        consumer1Context.stopRoute("route9");
        producerTemplate.sendBody("direct:start", "1234567890-2");

        // start service, so finish9 will receive the message again
        consumer1Context.startRoute("route9");
        producerTemplate.sendBody("direct:start", "1234567890-3");

        MockEndpoint.assertIsSatisfied(consumer1Context);
    }

    @Test
    public void testInstallUninstallBundle() throws Exception {
        for(int i = 0; i < 8; i++) {
            MockEndpoint finish = consumer1Context.getEndpoint("mock:finish" + i, MockEndpoint.class);
            finish.expectedMessageCount(0);
        }

        MockEndpoint finish9 = consumer1Context.getEndpoint("mock:finish9", MockEndpoint.class);
        finish9.expectedBodiesReceived("1234567890-1", "1234567890-3");

        ProducerTemplate producerTemplate = producerContext.createProducerTemplate();
        producerTemplate.sendBody("direct:start", "1234567890-1");

        // add new bundle with service that has highest ranking, so it has to receive the next sent message
        Bundle bundle = installBundle();
        bundle.start();

        CamelContext consumer2Context = getOsgiService(CamelContext.class, "(camel.context.symbolicname=" + getClass().getName() + ".consumer2)");
        MockEndpoint finish100 = consumer2Context.getEndpoint("mock:finish100", MockEndpoint.class);
        finish100.expectedBodiesReceived("1234567890-2");
        producerTemplate.sendBody("direct:start", "1234567890-2");

        // remove new bundle
        bundle.uninstall();
        producerTemplate.sendBody("direct:start", "1234567890-3");

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

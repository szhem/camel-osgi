package org.apache.camel.osgi.service;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.Processor;
import org.apache.camel.osgi.service.OsgiDefaultConsumer;
import org.apache.camel.osgi.service.OsgiDefaultEndpoint;
import org.apache.camel.osgi.service.OsgiDefaultProducer;
import org.apache.camel.osgi.service.util.BundleDelegatingClassLoader;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class OsgiDefaultEndpointTest {

    @Test
    public void testCreateClassLoaderHasGetBundle() throws Exception {
        Bundle bundle = mock(Bundle.class);
        ClassLoader classLoader = new BundleDelegatingClassLoader(bundle, getClass().getClassLoader());

        CamelContext camelContext = mock(CamelContext.class);
        when(camelContext.getApplicationContextClassLoader()).thenReturn(classLoader);

        Component component = mock(Component.class);
        when(component.getCamelContext()).thenReturn(camelContext);

        new OsgiDefaultEndpoint("osgi:test", component);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateClassLoaderNoGetBundle() throws Exception {
        CamelContext camelContext = mock(CamelContext.class);
        when(camelContext.getApplicationContextClassLoader()).thenReturn(getClass().getClassLoader());

        Component component = mock(Component.class);
        when(component.getCamelContext()).thenReturn(camelContext);

        new OsgiDefaultEndpoint("osgi:test", component);
    }

    @Test
    public void testCreateBundleRefIsClassLoader() throws Exception {
        Bundle bundle = mock(Bundle.class);

        ClassLoader classLoader = mock(ClassLoader.class, withSettings().extraInterfaces(BundleReference.class));
        when(((BundleReference) classLoader).getBundle()).thenReturn(bundle);

        CamelContext camelContext = mock(CamelContext.class);
        when(camelContext.getApplicationContextClassLoader()).thenReturn(classLoader);

        Component component = mock(Component.class);
        when(component.getCamelContext()).thenReturn(camelContext);

        new OsgiDefaultEndpoint("osgi:test", component);
    }

    @Test
    public void testCreateProducer() throws Exception {
        OsgiDefaultEndpoint endpoint = createEndpoint();
        endpoint.setProps(Collections.<String, Object>singletonMap("key", "value"));
        assertThat(endpoint.createProducer(), instanceOf(OsgiDefaultProducer.class));
    }

    @Test
    public void testCreateProducerNoProps() throws Exception {
        OsgiDefaultEndpoint endpoint = createEndpoint();
        assertThat(endpoint.createProducer(), instanceOf(OsgiDefaultProducer.class));
    }

    @Test
    public void testCreateConsumer() throws Exception {
        Processor processor = mock(Processor.class);
        OsgiDefaultEndpoint endpoint = createEndpoint();
        assertThat(endpoint.createConsumer(processor), instanceOf(OsgiDefaultConsumer.class));
    }

    private OsgiDefaultEndpoint createEndpoint() {
        Bundle bundle = mock(Bundle.class);
        ClassLoader classLoader = new BundleDelegatingClassLoader(bundle, getClass().getClassLoader());

        CamelContext camelContext = mock(CamelContext.class);
        when(camelContext.getApplicationContextClassLoader()).thenReturn(classLoader);

        Component component = mock(Component.class);
        when(component.getCamelContext()).thenReturn(camelContext);

        return new OsgiDefaultEndpoint("osgi:test", component);
    }
}

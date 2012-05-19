package org.apache.camel.osgi.service;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.Processor;
import org.apache.camel.osgi.service.OsgiRandomEndpoint;
import org.apache.camel.osgi.service.OsgiRandomProducer;
import org.apache.camel.osgi.service.util.BundleDelegatingClassLoader;
import org.junit.Test;
import org.osgi.framework.Bundle;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class OsgiRandomEndpointTest {

    @Test(expected = UnsupportedOperationException.class)
    public void testCreateConsumer() throws Exception {
        OsgiRandomEndpoint endpoint = createEndpoint();

        Processor processor = mock(Processor.class);
        endpoint.createConsumer(processor);
    }

    @Test
    public void testCreateProducer() throws Exception {
        OsgiRandomEndpoint endpoint = createEndpoint();
        assertThat(endpoint.createProducer(), instanceOf(OsgiRandomProducer.class));
    }

    private OsgiRandomEndpoint createEndpoint() throws Exception {
        Bundle bundle = mock(Bundle.class);
        ClassLoader classLoader = new BundleDelegatingClassLoader(bundle, getClass().getClassLoader());

        CamelContext camelContext = mock(CamelContext.class);
        when(camelContext.getApplicationContextClassLoader()).thenReturn(classLoader);

        Component component = mock(Component.class);
        when(component.getCamelContext()).thenReturn(camelContext);

        return new OsgiRandomEndpoint("osgi:random:test", component);
    }

}

package org.apache.camel.osgi.service;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.Processor;
import org.apache.camel.osgi.service.OsgiMulticastEndpoint;
import org.apache.camel.osgi.service.OsgiMulticastProducer;
import org.apache.camel.osgi.service.util.BundleDelegatingClassLoader;
import org.junit.Test;
import org.osgi.framework.Bundle;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OsgiMulticastEndpointTest {

    @Test(expected = UnsupportedOperationException.class)
    public void testCreateConsumer() throws Exception {
        OsgiMulticastEndpoint endpoint = createEndpoint();

        Processor processor = mock(Processor.class);
        endpoint.createConsumer(processor);
    }

    @Test
    public void testCreateProducer() throws Exception {
        OsgiMulticastEndpoint endpoint = createEndpoint();
        assertThat(endpoint.createProducer(), instanceOf(OsgiMulticastProducer.class));
    }

    private OsgiMulticastEndpoint createEndpoint() {
        Bundle bundle = mock(Bundle.class);
        ClassLoader classLoader = new BundleDelegatingClassLoader(bundle, getClass().getClassLoader());

        CamelContext camelContext = mock(CamelContext.class);
        when(camelContext.getApplicationContextClassLoader()).thenReturn(classLoader);

        Component component = mock(Component.class);
        when(component.getCamelContext()).thenReturn(camelContext);

        return new OsgiMulticastEndpoint("osgi:multicast:test", component);
    }

}

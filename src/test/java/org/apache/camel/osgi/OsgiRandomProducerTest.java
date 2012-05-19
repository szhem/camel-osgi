package org.apache.camel.osgi;

import org.apache.camel.CamelContext;
import org.apache.camel.processor.loadbalancer.RandomLoadBalancer;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OsgiRandomProducerTest {

    @Test
    public void testCreateProcessor() throws Exception {
        CamelContext camelContext = mock(CamelContext.class);

        OsgiDefaultEndpoint endpoint = mock(OsgiDefaultEndpoint.class);
        when(endpoint.getCamelContext()).thenReturn(camelContext);

        OsgiRandomProducer producer = new OsgiRandomProducer(endpoint, Collections.<String, Object>emptyMap());
        assertThat(producer.createProcessor(), instanceOf(RandomLoadBalancer.class));
    }

}

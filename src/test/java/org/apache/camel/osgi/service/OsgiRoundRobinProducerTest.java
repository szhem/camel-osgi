package org.apache.camel.osgi.service;

import org.apache.camel.CamelContext;
import org.apache.camel.osgi.service.OsgiRoundRobinEndpoint;
import org.apache.camel.osgi.service.OsgiRoundRobinProducer;
import org.apache.camel.processor.loadbalancer.RoundRobinLoadBalancer;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OsgiRoundRobinProducerTest {

    @Test
    public void testCreateProcessor() throws Exception {
        CamelContext camelContext = mock(CamelContext.class);

        OsgiRoundRobinEndpoint endpoint = mock(OsgiRoundRobinEndpoint.class);
        when(endpoint.getCamelContext()).thenReturn(camelContext);

        OsgiRoundRobinProducer producer = new OsgiRoundRobinProducer(endpoint, Collections.<String, Object>emptyMap());
        assertThat(producer.createProcessor(), instanceOf(RoundRobinLoadBalancer.class));
    }

}

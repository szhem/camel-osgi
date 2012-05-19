package org.apache.camel.osgi;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.osgi.util.OsgiServiceList;
import org.apache.camel.processor.loadbalancer.LoadBalancer;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OsgiDefaultProducerTest {

    @Test
    public void testProcess() throws Exception {
        Processor processor = mock(Processor.class);

        OsgiDefaultProducer producer = createProducer();
        producer.processor = processor;

        Exchange exchange = mock(Exchange.class);
        producer.process(exchange);

        verify(processor).process(same(exchange));
    }

    @Test
    public void testCreateProcessor() throws Exception {
        OsgiDefaultProducer producer = createProducer();

        Processor processor = producer.createProcessor();
        assertThat(processor, instanceOf(OsgiDefaultLoadBalancer.class));
        assertThat(((LoadBalancer) processor).getProcessors(), instanceOf(OsgiServiceList.class));
    }

    @Test
    public void testDoStart() throws Exception {

    }

    @Test
    public void testDoStop() throws Exception {

    }

    @Test
    public void testDoShutdown() throws Exception {

    }
    
    private OsgiDefaultProducer createProducer() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);
        
        OsgiDefaultEndpoint endpoint = mock(OsgiDefaultEndpoint.class);
        when(endpoint.getApplicationBundleContext()).thenReturn(bundleContext);
        when(endpoint.getComponentClassLoader()).thenReturn(getClass().getClassLoader());

        OsgiDefaultProducer producer = new OsgiDefaultProducer(endpoint, Collections.<String, Object>emptyMap());
        return producer;
    }

}

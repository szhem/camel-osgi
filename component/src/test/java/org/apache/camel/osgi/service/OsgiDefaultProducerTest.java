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

package org.apache.camel.osgi.service;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.osgi.service.util.OsgiServiceList;
import org.apache.camel.processor.loadbalancer.LoadBalancer;
import org.apache.camel.util.ServiceHelper;
import org.junit.Test;
import org.mockito.Matchers;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceListener;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OsgiDefaultProducerTest {

    @Test
    public void testProcess() throws Exception {
        Processor processor = mock(Processor.class);

        OsgiDefaultProducer producer = createProducer(Collections.<String, Object>emptyMap());
        producer.processor = processor;

        Exchange exchange = mock(Exchange.class);
        producer.process(exchange);

        verify(processor).process(same(exchange));
    }

    @Test
    public void testCreateProcessor() throws Exception {
        OsgiDefaultProducer producer = createProducer(Collections.<String, Object>emptyMap());

        Processor processor = producer.createProcessor();
        assertThat(processor, instanceOf(OsgiDefaultLoadBalancer.class));
        assertThat(((LoadBalancer) processor).getProcessors(), instanceOf(OsgiServiceList.class));
    }

    @Test
    public void testDoStart() throws Exception {
        OsgiDefaultProducer producer = createProducer(Collections.singletonMap("key", (Object) "value"));
        ServiceHelper.startService(producer);

        verify(producer.getApplicationBundleContext()).addServiceListener(Matchers.any(ServiceListener.class), eq("(&(key=value))"));
    }

    @Test
    public void testDoStartTwice() throws Exception {
        OsgiDefaultProducer producer = createProducer(Collections.singletonMap("key", (Object) "value"));
        ServiceHelper.startService(producer);
        ServiceHelper.startService(producer);

        verify(producer.getApplicationBundleContext()).addServiceListener(Matchers.any(ServiceListener.class), eq("(&(key=value))"));
    }

    @Test
    public void testDoStop() throws Exception {
        OsgiDefaultProducer producer = createProducer(Collections.singletonMap("key", (Object) "value"));
        ServiceHelper.startService(producer);
        ServiceHelper.stopService(producer);

        verify(producer.getApplicationBundleContext()).removeServiceListener(Matchers.any(ServiceListener.class));
    }

    @Test
    public void testDoStopTwice() throws Exception {
        OsgiDefaultProducer producer = createProducer(Collections.singletonMap("key", (Object) "value"));
        ServiceHelper.startService(producer);
        ServiceHelper.stopService(producer);
        ServiceHelper.stopService(producer);

        verify(producer.getApplicationBundleContext()).removeServiceListener(Matchers.any(ServiceListener.class));
    }

    private OsgiDefaultProducer createProducer(Map<String, Object> props) throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);
        
        OsgiDefaultEndpoint endpoint = mock(OsgiDefaultEndpoint.class);
        when(endpoint.getApplicationBundleContext()).thenReturn(bundleContext);
        when(endpoint.getComponentClassLoader()).thenReturn(getClass().getClassLoader());

        return new OsgiDefaultProducer(endpoint, props);
    }

}

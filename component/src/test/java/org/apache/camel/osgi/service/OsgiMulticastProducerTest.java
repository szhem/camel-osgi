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

import org.apache.camel.CamelContext;
import org.apache.camel.processor.MulticastProcessor;
import org.apache.camel.spi.ExecutorServiceManager;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.ExecutorService;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OsgiMulticastProducerTest {

    @Test
    public void testCreate() throws Exception {
        CamelContext camelContext = mock(CamelContext.class);

        OsgiDefaultEndpoint endpoint = mock(OsgiDefaultEndpoint.class);
        when(endpoint.getCamelContext()).thenReturn(camelContext);

        OsgiMulticastProducer producer = new OsgiMulticastProducer(
            endpoint, Collections.<String, Object>emptyMap(), null, false, null, false, false, false, 1, null);

        assertThat(producer.getAggregationStrategy(), nullValue());
        assertThat(producer.isParallelProcessing(), equalTo(false));
        assertThat(producer.getExecutorService(), nullValue());
        assertThat(producer.isStreaming(), equalTo(false));
        assertThat(producer.isStopOnException(), equalTo(false));
        assertThat(producer.getTimeout(), equalTo(1L));
        assertThat(producer.getOnPrepare(), nullValue());
    }

    @Test
    public void testCreateParallelProcessing() throws Exception {
        CamelContext camelContext = mock(CamelContext.class);

        OsgiDefaultEndpoint endpoint = mock(OsgiDefaultEndpoint.class);
        when(endpoint.getCamelContext()).thenReturn(camelContext);

        ExecutorService executor = mock(ExecutorService.class);

        OsgiMulticastProducer producer = new OsgiMulticastProducer(
            endpoint, Collections.<String, Object>emptyMap(), null, true, executor, false, false, false, 1, null);

        assertThat(producer.getAggregationStrategy(), nullValue());
        assertThat(producer.isParallelProcessing(), equalTo(true));
        assertThat(producer.getExecutorService(), sameInstance(executor));
        assertThat(producer.isStreaming(), equalTo(false));
        assertThat(producer.isStopOnException(), equalTo(false));
        assertThat(producer.getTimeout(), equalTo(1L));
        assertThat(producer.getOnPrepare(), nullValue());
    }

    @Test
    public void testCreateParallelProcessingNoExecutorService() throws Exception {
        ExecutorService executor = mock(ExecutorService.class);

        ExecutorServiceManager executorManager = mock(ExecutorServiceManager.class);
        when(executorManager.newDefaultThreadPool(anyObject(), anyString())).thenReturn(executor);

        CamelContext camelContext = mock(CamelContext.class);
        when(camelContext.getExecutorServiceManager()).thenReturn(executorManager);

        OsgiDefaultEndpoint endpoint = mock(OsgiDefaultEndpoint.class);
        when(endpoint.getCamelContext()).thenReturn(camelContext);

        OsgiMulticastProducer producer = new OsgiMulticastProducer(
            endpoint, Collections.<String, Object>emptyMap(), null, true, null, false, false, false, 1, null);

        assertThat(producer.getAggregationStrategy(), nullValue());
        assertThat(producer.isParallelProcessing(), equalTo(true));
        assertThat(producer.getExecutorService(), sameInstance(executor));
        assertThat(producer.isStreaming(), equalTo(false));
        assertThat(producer.isStopOnException(), equalTo(false));
        assertThat(producer.getTimeout(), equalTo(1L));
        assertThat(producer.getOnPrepare(), nullValue());
    }

    @Test
    public void testCreateProcessor() throws Exception {
        ExecutorService executor = mock(ExecutorService.class);

        ExecutorServiceManager executorManager = mock(ExecutorServiceManager.class);
        when(executorManager.newDefaultThreadPool(anyObject(), anyString())).thenReturn(executor);

        CamelContext camelContext = mock(CamelContext.class);
        when(camelContext.getExecutorServiceManager()).thenReturn(executorManager);

        OsgiDefaultEndpoint endpoint = mock(OsgiDefaultEndpoint.class);
        when(endpoint.getCamelContext()).thenReturn(camelContext);

        OsgiMulticastProducer producer = new OsgiMulticastProducer(
            endpoint, Collections.<String, Object>emptyMap(), null, true, null, false, true, true, 1, null);

        MulticastProcessor processor = (MulticastProcessor) producer.createProcessor();
        assertThat(processor.getAggregationStrategy(), nullValue());
        assertThat(processor.isParallelProcessing(), equalTo(true));
        assertThat(processor.isStreaming(), equalTo(true));
        assertThat(processor.isStopOnException(), equalTo(true));
        assertThat(processor.getTimeout(), equalTo(1L));
    }

    @Test
    public void testDoShutdown() throws Exception {
        ExecutorServiceManager executorManager = mock(ExecutorServiceManager.class);

        CamelContext camelContext = mock(CamelContext.class);
        when(camelContext.getExecutorServiceManager()).thenReturn(executorManager);

        OsgiDefaultEndpoint endpoint = mock(OsgiDefaultEndpoint.class);
        when(endpoint.getCamelContext()).thenReturn(camelContext);

        ExecutorService executor = mock(ExecutorService.class);
        when(executorManager.newDefaultThreadPool(anyObject(), anyString())).thenReturn(executor);

        OsgiMulticastProducer producer = new OsgiMulticastProducer(
            endpoint, Collections.<String, Object>emptyMap(), null, true, null, false, false, false, 0, null);

        producer.doShutdown();

        verify(executorManager).shutdownNow(same(executor));
    }

}

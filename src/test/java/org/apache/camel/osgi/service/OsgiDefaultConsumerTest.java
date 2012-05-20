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
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.Service;
import org.apache.camel.osgi.service.OsgiComponent;
import org.apache.camel.osgi.service.OsgiDefaultConsumer;
import org.apache.camel.osgi.service.OsgiDefaultEndpoint;
import org.apache.camel.util.ServiceHelper;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class OsgiDefaultConsumerTest {

    @Test
    public void testDoStart() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);

        OsgiDefaultEndpoint endpoint = mock(OsgiDefaultEndpoint.class);
        when(endpoint.getApplicationBundleContext()).thenReturn(bundleContext);

        Service processor = mock(Service.class, withSettings().extraInterfaces(Processor.class));
        Map<String, Object> props = Collections.singletonMap("key", (Object) "value");

        OsgiDefaultConsumer consumer = new OsgiDefaultConsumer(endpoint, (Processor) processor, props);

        ServiceHelper.startService(consumer);

        verify(processor).start();
        verify(bundleContext)
                .registerService(eq(OsgiComponent.OBJECT_CLASS), same(consumer), eq(new Hashtable<String, Object>(props)));
    }

    @Test
    public void testDoStartTwice() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);

        OsgiDefaultEndpoint endpoint = mock(OsgiDefaultEndpoint.class);
        when(endpoint.getApplicationBundleContext()).thenReturn(bundleContext);

        Service processor = mock(Service.class, withSettings().extraInterfaces(Processor.class));
        Map<String, Object> props = Collections.singletonMap("key", (Object) "value");

        OsgiDefaultConsumer consumer = new OsgiDefaultConsumer(endpoint, (Processor) processor, props);

        ServiceHelper.startService(consumer);
        ServiceHelper.startService(consumer);

        verify(processor).start();
        verify(bundleContext)
                .registerService(eq(OsgiComponent.OBJECT_CLASS), same(consumer), eq(new Hashtable<String, Object>(props)));
    }

    @Test
    public void testDoStop() throws Exception {
        ServiceRegistration registration = mock(ServiceRegistration.class);

        BundleContext bundleContext = mock(BundleContext.class);
        when(bundleContext.registerService(eq(OsgiComponent.OBJECT_CLASS), anyObject(), any(Dictionary.class)))
                .thenReturn(registration);

        OsgiDefaultEndpoint endpoint = mock(OsgiDefaultEndpoint.class);
        when(endpoint.getApplicationBundleContext()).thenReturn(bundleContext);

        Service processor = mock(Service.class, withSettings().extraInterfaces(Processor.class));

        OsgiDefaultConsumer consumer = new OsgiDefaultConsumer(
                endpoint, (Processor) processor, Collections.<String, Object>emptyMap());
        ServiceHelper.startService(consumer);
        ServiceHelper.stopService(consumer);

        verify(processor).stop();
        verify(registration).unregister();
    }

    @Test
    public void testDoStopTwice() throws Exception {
        ServiceRegistration registration = mock(ServiceRegistration.class);

        BundleContext bundleContext = mock(BundleContext.class);
        when(bundleContext.registerService(eq(OsgiComponent.OBJECT_CLASS), anyObject(), any(Dictionary.class)))
                .thenReturn(registration);

        OsgiDefaultEndpoint endpoint = mock(OsgiDefaultEndpoint.class);
        when(endpoint.getApplicationBundleContext()).thenReturn(bundleContext);

        Service processor = mock(Service.class, withSettings().extraInterfaces(Processor.class));

        OsgiDefaultConsumer consumer = new OsgiDefaultConsumer(
                endpoint, (Processor) processor, Collections.<String, Object>emptyMap());
        ServiceHelper.startService(consumer);
        ServiceHelper.stopService(consumer);
        ServiceHelper.stopService(consumer);

        verify(processor).stop();
        verify(registration).unregister();
    }

    @Test
    public void testDoResume() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);

        OsgiDefaultEndpoint endpoint = mock(OsgiDefaultEndpoint.class);
        when(endpoint.getApplicationBundleContext()).thenReturn(bundleContext);

        Service processor = mock(Service.class, withSettings().extraInterfaces(Processor.class));
        Map<String, Object> props = Collections.singletonMap("key", (Object) "value");

        OsgiDefaultConsumer consumer = new OsgiDefaultConsumer(endpoint, (Processor) processor, props);
        ServiceHelper.startService(consumer);
        ServiceHelper.suspendService(consumer);
        ServiceHelper.resumeService(consumer);

        verify(processor).start();
        verify(bundleContext, times(2))
                .registerService(eq(OsgiComponent.OBJECT_CLASS), same(consumer), eq(new Hashtable<String, Object>(props)));
    }

    @Test
    public void testDoSuspend() throws Exception {
        ServiceRegistration registration = mock(ServiceRegistration.class);

        BundleContext bundleContext = mock(BundleContext.class);
        when(bundleContext.registerService(eq(OsgiComponent.OBJECT_CLASS), anyObject(), any(Dictionary.class)))
                .thenReturn(registration);

        OsgiDefaultEndpoint endpoint = mock(OsgiDefaultEndpoint.class);
        when(endpoint.getApplicationBundleContext()).thenReturn(bundleContext);

        Service processor = mock(Service.class, withSettings().extraInterfaces(Processor.class));

        OsgiDefaultConsumer consumer = new OsgiDefaultConsumer(
                endpoint, (Processor) processor, Collections.<String, Object>emptyMap());
        ServiceHelper.startService(consumer);
        ServiceHelper.suspendService(consumer);
        ServiceHelper.resumeService(consumer);
        ServiceHelper.suspendService(consumer);

        verify(processor, never()).stop();
        verify(registration, times(2)).unregister();
    }

    @Test
    public void testProcess() throws Exception {
        Map<String, Object> exchangeProps = new HashMap<String, Object>();

        Exchange exchange = mock(Exchange.class, RETURNS_MOCKS);
        when(exchange.getPattern()).thenReturn(ExchangePattern.InOut);
        when(exchange.getProperties()).thenReturn(exchangeProps);

        Processor processor = mock(Processor.class);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Exchange exchange = (Exchange) invocation.getArguments()[0];
                exchange.setProperty("Hello", "World");
                return null;
            }
        }).when(processor).process(any(Exchange.class));

        OsgiDefaultEndpoint endpoint = mock(OsgiDefaultEndpoint.class);
        OsgiDefaultConsumer consumer = new OsgiDefaultConsumer(endpoint, processor, Collections.<String, Object>emptyMap());
        consumer.process(exchange);

        verify(processor).process(any(Exchange.class));

        assertThat((String) exchangeProps.get("Hello"), equalTo("World"));
    }

    @Test
    public void testCopyExchange() throws Exception {
        Processor processor = mock(Processor.class);
        OsgiDefaultEndpoint endpoint = mock(OsgiDefaultEndpoint.class);
        OsgiDefaultConsumer consumer = new OsgiDefaultConsumer(endpoint, processor, Collections.<String, Object>emptyMap());

        Exchange exchange = mock(Exchange.class, RETURNS_MOCKS);
        when(exchange.getPattern()).thenReturn(ExchangePattern.OutOptionalIn);
        when(exchange.getExchangeId()).thenReturn("12345");

        Exchange copy = consumer.copyExchange(exchange);

        verify(exchange, atLeastOnce()).getPattern();
        verify(exchange, atLeastOnce()).getExchangeId();
        verify(exchange, atLeastOnce()).getIn();
        verify(exchange, atLeastOnce()).getException();

        assertThat(copy.getPattern(), sameInstance(ExchangePattern.OutOptionalIn));
        assertThat(copy.getProperty(Exchange.CORRELATION_ID, String.class), equalTo("12345"));
    }

}

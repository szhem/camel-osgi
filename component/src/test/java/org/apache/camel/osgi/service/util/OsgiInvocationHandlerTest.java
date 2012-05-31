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

package org.apache.camel.osgi.service.util;

import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OsgiInvocationHandlerTest {
    
    @Test
    public void testInvoke() throws Throwable {
        List<?> service = new ArrayList<Object>();

        ServiceReference reference = mock(ServiceReference.class);

        BundleContext bundleContext = mock(BundleContext.class);
        when(bundleContext.getService(same(reference))).thenReturn(service);

        OsgiInvocationHandler handler = new OsgiInvocationHandler(bundleContext, reference);
        handler.invoke(reference, List.class.getMethod("add", Object.class), new Object[] {"test"});

        verify(bundleContext).getService(same(reference));
        verify(bundleContext).ungetService(same(reference));

        assertThat((String) service.get(0), equalTo("test"));
    }

    @Test
    public void testInvokeEquals() throws Throwable {
        List<?> service = new ArrayList<Object>();

        ServiceReference reference = mock(ServiceReference.class);

        BundleContext bundleContext = mock(BundleContext.class);
        when(bundleContext.getService(same(reference))).thenReturn(service);

        OsgiInvocationHandler handler = new OsgiInvocationHandler(bundleContext, reference);
        Object answer = handler.invoke(reference, Object.class.getMethod("equals", Object.class),
            new Object[]{reference});

        assertThat((Boolean) answer, equalTo(true));
    }

    @Test
    public void testInvokeHashCode() throws Throwable {
        List<?> service = new ArrayList<Object>();

        ServiceReference reference = mock(ServiceReference.class);

        BundleContext bundleContext = mock(BundleContext.class);
        when(bundleContext.getService(same(reference))).thenReturn(service);

        OsgiInvocationHandler handler = new OsgiInvocationHandler(bundleContext, reference);
        Object answer = handler.invoke(reference, Object.class.getMethod("hashCode"), new Object[]{reference});

        assertThat((Integer) answer, equalTo(reference.hashCode()));
    }

    @Test
    public void testInvokeGetReference() throws Throwable {
        List<?> service = new ArrayList<Object>();

        ServiceReference reference = mock(ServiceReference.class);

        BundleContext bundleContext = mock(BundleContext.class);
        when(bundleContext.getService(same(reference))).thenReturn(service);

        OsgiInvocationHandler handler = new OsgiInvocationHandler(bundleContext, reference);
        Object answer = handler.invoke(reference, OsgiProxy.class.getMethod("getReference"), new Object[0]);

        assertThat((ServiceReference) answer, sameInstance(reference));
    }

    @Test(expected = IllegalStateException.class)
    public void testInvokeNoService() throws Throwable {
        ServiceReference reference = mock(ServiceReference.class);
        BundleContext bundleContext = mock(BundleContext.class);

        OsgiInvocationHandler handler = new OsgiInvocationHandler(bundleContext, reference);
        handler.invoke(reference, List.class.getMethod("add", Object.class), new Object[]{"test"});
    }
    
}

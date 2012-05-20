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
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OsgiDefaultProxyCreatorTest {
    
    @Test
    public void testCreateProxy() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);
        
        ServiceReference reference = mock(ServiceReference.class, RETURNS_MOCKS);
        when(reference.getProperty(Constants.OBJECTCLASS)).thenReturn(new String[] {List.class.getName()});

        OsgiDefaultProxyCreator creator = new OsgiDefaultProxyCreator();
        Object proxy = creator.createProxy(bundleContext, reference, getClass().getClassLoader());

        assertThat(proxy, instanceOf(OsgiProxy.class));
        assertThat(proxy, instanceOf(ServiceReference.class));
        assertThat(proxy, instanceOf(List.class));

        verify(reference).getBundle();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProxyNoBundle() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);
        ServiceReference reference = mock(ServiceReference.class);

        OsgiDefaultProxyCreator creator = new OsgiDefaultProxyCreator();
        creator.createProxy(bundleContext, reference, getClass().getClassLoader());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProxyNoClass() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);

        ServiceReference reference = mock(ServiceReference.class, RETURNS_MOCKS);
        when(reference.getProperty(Constants.OBJECTCLASS)).thenReturn(new String[] {List.class.getName(), "Undefined"});

        OsgiDefaultProxyCreator creator = new OsgiDefaultProxyCreator();
        creator.createProxy(bundleContext, reference, getClass().getClassLoader());
    }

}

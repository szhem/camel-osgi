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
import org.apache.camel.osgi.service.OsgiComponent;
import org.apache.camel.osgi.service.OsgiDefaultEndpoint;
import org.apache.camel.osgi.service.OsgiEndpointType;
import org.apache.camel.osgi.service.OsgiMulticastEndpoint;
import org.apache.camel.osgi.service.OsgiRandomEndpoint;
import org.apache.camel.osgi.service.OsgiRoundRobinEndpoint;
import org.apache.camel.osgi.service.util.BundleDelegatingClassLoader;
import org.junit.Test;
import org.osgi.framework.Bundle;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OsgiEndpointTypeTest {

    @Test
    public void testGetType() throws Exception {
        OsgiEndpointType endpointType = OsgiEndpointType.fromPath("");
        assertThat(endpointType, sameInstance(OsgiEndpointType.DEFAULT));

        endpointType = OsgiEndpointType.fromPath("default:test");
        assertThat(endpointType, sameInstance(OsgiEndpointType.DEFAULT));

        endpointType = OsgiEndpointType.fromPath("unknown:test");
        assertThat(endpointType, sameInstance(OsgiEndpointType.DEFAULT));

        endpointType = OsgiEndpointType.fromPath("multicast:test");
        assertThat(endpointType, sameInstance(OsgiEndpointType.MULTICAST));

        endpointType = OsgiEndpointType.fromPath("roundrobin:test");
        assertThat(endpointType, sameInstance(OsgiEndpointType.ROUNDROBIN));

        endpointType = OsgiEndpointType.fromPath("random:test");
        assertThat(endpointType, sameInstance(OsgiEndpointType.RANDOM));
    }

    @Test
    public void testGetName() throws Exception {
        assertThat(OsgiEndpointType.DEFAULT.getName("test"), equalTo("test"));
        assertThat(OsgiEndpointType.DEFAULT.getName("default:test"), equalTo("test"));
        assertThat(OsgiEndpointType.MULTICAST.getName("multicast:test"), equalTo("test"));
        assertThat(OsgiEndpointType.ROUNDROBIN.getName("roundrobin:test"), equalTo("test"));
        assertThat(OsgiEndpointType.RANDOM.getName("random:test"), equalTo("test"));
    }

    @Test
    public void testCreateEndpoint() throws Exception {
        Bundle bundle = mock(Bundle.class);
        BundleDelegatingClassLoader classLoader = new BundleDelegatingClassLoader(bundle, getClass().getClassLoader());

        CamelContext camelContext = mock(CamelContext.class);
        when(camelContext.getApplicationContextClassLoader()).thenReturn(classLoader);

        OsgiComponent comp = new OsgiComponent();
        comp.setCamelContext(camelContext);

        assertThat(OsgiEndpointType.DEFAULT.createEndpoint("osgi:test", comp), instanceOf(OsgiDefaultEndpoint.class));
        assertThat(OsgiEndpointType.DEFAULT.createEndpoint("osgi:default:test", comp), instanceOf(OsgiDefaultEndpoint.class));
        assertThat(OsgiEndpointType.DEFAULT.createEndpoint("osgi:unknown:test", comp), instanceOf(OsgiDefaultEndpoint.class));
        assertThat(OsgiEndpointType.MULTICAST.createEndpoint("osgi:multicast:test", comp), instanceOf(OsgiMulticastEndpoint.class));
        assertThat(OsgiEndpointType.ROUNDROBIN.createEndpoint("osgi:roundrobin:test", comp), instanceOf(OsgiRoundRobinEndpoint.class));
        assertThat(OsgiEndpointType.RANDOM.createEndpoint("osgi:random:test", comp), instanceOf(OsgiRandomEndpoint.class));
    }

}

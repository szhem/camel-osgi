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
import org.apache.camel.Component;
import org.apache.camel.Processor;
import org.apache.camel.osgi.service.util.BundleDelegatingClassLoader;
import org.junit.Test;
import org.osgi.framework.Bundle;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class OsgiRoundRobinEndpointTest {

    @Test(expected = UnsupportedOperationException.class)
    public void testCreateConsumer() throws Exception {
        OsgiRoundRobinEndpoint endpoint = createEndpoint();

        Processor processor = mock(Processor.class);
        endpoint.createConsumer(processor);
    }

    @Test
    public void testCreateProducer() throws Exception {
        OsgiRoundRobinEndpoint endpoint = createEndpoint();
        assertThat(endpoint.createProducer(), instanceOf(OsgiRoundRobinProducer.class));
    }

    private OsgiRoundRobinEndpoint createEndpoint() throws Exception {
        Bundle bundle = mock(Bundle.class);
        ClassLoader classLoader = new BundleDelegatingClassLoader(bundle, getClass().getClassLoader());

        CamelContext camelContext = mock(CamelContext.class);
        when(camelContext.getApplicationContextClassLoader()).thenReturn(classLoader);

        Component component = mock(Component.class);
        when(component.getCamelContext()).thenReturn(camelContext);

        return new OsgiRoundRobinEndpoint("osgi:roundrobin:test", component);
    }

}

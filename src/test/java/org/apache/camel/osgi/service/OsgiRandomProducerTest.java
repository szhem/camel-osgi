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
import org.apache.camel.osgi.service.OsgiDefaultEndpoint;
import org.apache.camel.osgi.service.OsgiRandomProducer;
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

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

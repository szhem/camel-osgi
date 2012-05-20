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
import org.apache.camel.processor.loadbalancer.QueueLoadBalancer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The {@code OsgiDefaultLoadBalancer} is the load balancer that always selects most actual OSGi service, i.e. with
 * the highest ranking.
 * <p/>
 * As a rule processors to select from must implement {@link org.osgi.framework.ServiceReference} to work as expected.
 */
public class OsgiDefaultLoadBalancer extends QueueLoadBalancer {

    @Override
    protected synchronized Processor chooseProcessor(List<Processor> processors, Exchange exchange) {
        return choose(processors);
    }

    @SuppressWarnings("unchecked")
    private Processor choose(Collection<?> processors) {
        return (Processor) Collections.max((Collection<? extends Comparable>) processors);
    }
}

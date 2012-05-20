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

import org.apache.camel.Processor;
import org.apache.camel.processor.loadbalancer.RandomLoadBalancer;

import java.util.List;
import java.util.Map;

/**
 * The {@code OsgiRandomProducer} is the producer that uses {@link RandomLoadBalancer} to send exchanges to OSGi
 * consumers.
 */
public class OsgiRandomProducer extends OsgiDefaultProducer {

    public OsgiRandomProducer(OsgiDefaultEndpoint endpoint, Map<String, Object> props) {
        super(endpoint, props);
    }

    @Override
    protected Processor createProcessor() {
        return new RandomLoadBalancer() {
            @Override
            public List<Processor> getProcessors() {
                return services;
            }
        };
    }

}

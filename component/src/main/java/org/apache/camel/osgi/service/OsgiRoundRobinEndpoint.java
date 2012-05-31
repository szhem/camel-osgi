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

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;

public class OsgiRoundRobinEndpoint extends OsgiDefaultEndpoint {

    public OsgiRoundRobinEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException(
                String.format("[%s] does not support consumerns. Use [%s] instead.",
                        getClass().getName(), getClass().getSuperclass().getName()));
    }

    @Override
    public Producer createProducer() throws Exception {
        return new OsgiRoundRobinProducer(this, getProps());
    }

}

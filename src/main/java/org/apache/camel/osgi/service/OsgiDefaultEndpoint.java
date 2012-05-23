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
import org.apache.camel.impl.DefaultEndpoint;
import org.osgi.framework.BundleContext;

import java.util.Collections;
import java.util.Map;

/**
 * The {@code OsgiDefaultEndpoint} is the endpoint type that provides point-to-point style of communication between
 * OSGi bundles selecting most actual consuming OSGi service, according to the specification, i.e. with the highest
 * ranking.
 */
public class OsgiDefaultEndpoint extends DefaultEndpoint {

    private final BundleContext applicationBundleContext;
    private final ClassLoader componentClassLoader;

    private Map<String, Object> props = Collections.emptyMap();

    public OsgiDefaultEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
        this.componentClassLoader = getClass().getClassLoader();
        this.applicationBundleContext = Activator.BUNDLE_CONTEXT.get();
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new OsgiDefaultConsumer(this, processor, getProps());
    }

    @Override
    public Producer createProducer() throws Exception {
        return new OsgiDefaultProducer(this, getProps());
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public OsgiComponent getComponent() {
        return (OsgiComponent) super.getComponent();
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }

    protected BundleContext getApplicationBundleContext() {
        return applicationBundleContext;
    }

    protected ClassLoader getComponentClassLoader() {
        return componentClassLoader;
    }
}

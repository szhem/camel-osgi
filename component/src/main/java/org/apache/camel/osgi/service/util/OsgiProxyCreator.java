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

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public interface OsgiProxyCreator {

    /**
     * Creates a dynamic instance that proxies calls to some OSGi service associated with the corresponding service
     * reference.
     * <p/>
     * The returned object implements {@link ServiceReference} and {@link OsgiProxy} interfaces to easily access the
     * functionality of the original instance of {@link ServiceReference}.
     *
     * @param bundleContext an instance of {@link BundleContext} to get the service associated with the provided reference.
     * @param reference a reference to the service to lookup
     * @param classLoader the {@link ClassLoader} to be used to load interfaces that are implemented by the exported
     * OSGi service to be implemented dynamically
     *
     * @param <T> the type to cast the created proxy to
     *
     * @return dynamic proxy for an exported OSGi service
     *
     * @throws IllegalArgumentException if the provided service reference is invalid or some of the interfaces implemented
     * by the original service cannot be loaded
     */
    <T> T createProxy(BundleContext bundleContext, ServiceReference reference, ClassLoader classLoader);
    
}

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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code OsgiDefaultProxyCreator} is the default implementation of the {@link OsgiProxyCreator}.
 * <p/>
 * It uses {@link OsgiInvocationHandler} to handle calls to the exported OSGi service.
 *
 * @see OsgiInvocationHandler
 */
public class OsgiDefaultProxyCreator implements OsgiProxyCreator {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createProxy(BundleContext bundleContext, ServiceReference reference, ClassLoader classLoader) {
        Bundle exportingBundle = reference.getBundle();
        if(exportingBundle == null) {
            throw new IllegalArgumentException(String.format("Service [%s] has been unregistered", reference));
        }

        InvocationHandler handler = new OsgiInvocationHandler(bundleContext, reference);

        String[] classNames = (String[]) reference.getProperty(Constants.OBJECTCLASS);
        List<Class<?>> classes = new ArrayList<Class<?>>(classNames.length);
        
        for(String className : classNames) {
            try {
                Class<?> clazz = classLoader.loadClass(className);
                if (clazz.isInterface()) {
                    classes.add(clazz);
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(
                        String.format("Unable to found class [%s] with classloader [%s]", className, classLoader));
            }
        }
        classes.add(OsgiProxy.class);
        classes.add(ServiceReference.class);

        return (T) Proxy.newProxyInstance(classLoader, classes.toArray(new Class<?>[classes.size()]), handler);
    }

}

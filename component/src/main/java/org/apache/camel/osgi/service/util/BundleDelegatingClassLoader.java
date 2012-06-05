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

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 * The {@code BundleDelegatingClassLoader} is the {@code ClassLoader} that delegates all class and resource loading
 * calls to the specified bundle or to the specified {@code ClassLoader} as fallback if the requested class or resource
 * cannot be loaded by the bundle.
 */
public class BundleDelegatingClassLoader extends ClassLoader {
    private final Bundle bundle;
    private final ClassLoader classLoader;

    public BundleDelegatingClassLoader(Bundle bundle) {
        this(bundle, null);
    }

    public BundleDelegatingClassLoader(Bundle bundle, ClassLoader classLoader) {
        if (bundle == null) {
            throw new IllegalArgumentException("bundle: null");
        }
        this.bundle = bundle;
        this.classLoader = classLoader;
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return bundle.loadClass(name);
    }

    protected URL findResource(String name) {
        URL resource = bundle.getResource(name);
        if (classLoader != null && resource == null) {
            resource = classLoader.getResource(name);
        }
        return resource;
    }

    @SuppressWarnings("unchecked")
    protected Enumeration findResources(String name) throws IOException {
        Enumeration resources = bundle.getResources(name);
        if (classLoader != null && (resources == null || !resources.hasMoreElements())) {
            resources = classLoader.getResources(name);
        }
        return resources;
    }

    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz;
        try {
            clazz = findClass(name);
        } catch (ClassNotFoundException cnfe) {
            if (classLoader != null) {
                try {
                    clazz = classLoader.loadClass(name);
                } catch (ClassNotFoundException e) {
                    throw new ClassNotFoundException(name + " from bundle " + bundle.getBundleId() + " (" + bundle.getSymbolicName() + ")", cnfe);
                }
            } else {
                throw new ClassNotFoundException(name + " from bundle " + bundle.getBundleId() + " (" + bundle.getSymbolicName() + ")", cnfe);
            }
        }
        if (resolve) {
            resolveClass(clazz);
        }
        return clazz;
    }

    public Bundle getBundle() {
        return bundle;
    }

    @Override
    public String toString() {
        return String.format("BundleDelegatingClassLoader(%s)", bundle);
    }
}

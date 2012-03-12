package org.apache.camel.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public interface OsgiProxyCreator {

    /**
     * The returned object must implement {@link ServiceReference} and {@link OsgiProxy}
     *
     * @param bundleContext
     * @param reference
     * @param classLoader
     * @param <T>
     * @return
     */
    <T> T createProxy(BundleContext bundleContext, ServiceReference reference, ClassLoader classLoader);
    
}

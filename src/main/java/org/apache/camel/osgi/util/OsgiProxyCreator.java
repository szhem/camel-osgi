package org.apache.camel.osgi.util;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * The {@code OsgiProxyCreator} is the interface that must be implemented by classes that are able to create
 * {@link OsgiProxy OSGi proxies} for OSGi services dynamically.
 */
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

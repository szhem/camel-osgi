package org.apache.camel.osgi.util;

import org.osgi.framework.ServiceReference;

/**
 * The {@code OsgiProxy} is an interface to be implemented dynamically by proxy classes for OSGi services to easily
 * access the corresponding {@link ServiceReference}.
 */
public interface OsgiProxy {

    /**
     * Returns {@link ServiceReference} that is associated with the proxied OSGi service.
     *
     * @return {@link ServiceReference} instance
     */
    ServiceReference getReference();

}

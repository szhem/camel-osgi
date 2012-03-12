package org.apache.camel.osgi;

import org.osgi.framework.ServiceReference;

public interface OsgiProxy {

    ServiceReference getReference();

}

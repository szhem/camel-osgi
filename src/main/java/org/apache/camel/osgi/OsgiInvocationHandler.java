package org.apache.camel.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class OsgiInvocationHandler implements InvocationHandler {

    private BundleContext bundleContext;
    private ServiceReference reference;

    public OsgiInvocationHandler(BundleContext bundleContext, ServiceReference reference) {
        this.bundleContext = bundleContext;
        this.reference = reference;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.getMethod("equals", Object.class).equals(method)) {
            return proxy == args[0];
        }
        if (Object.class.getMethod("hashCode").equals(method)) {
            return System.identityHashCode(proxy);
        }
        if (OsgiProxy.class.getMethod("getReference").equals(method)) {
            return getReference();
        }
        if (method.getDeclaringClass() == ServiceReference.class) {
            return method.invoke(reference, args);
        }

        Object service = bundleContext.getService(reference);
        if(service == null) {
            throw new IllegalStateException(String.format("There is no service registered for reference [%s]", reference));
        }

        try {
            return method.invoke(service, args);
        } finally {
            bundleContext.ungetService(reference);
        }
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public ServiceReference getReference() {
        return reference;
    }
}

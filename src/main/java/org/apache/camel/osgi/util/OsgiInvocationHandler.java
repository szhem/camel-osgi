package org.apache.camel.osgi.util;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * The {@link OsgiInvocationHandler} is an instance of {@link InvocationHandler} that is responsible to handle calls
 * to the original OSGi service.
 */
public class OsgiInvocationHandler implements InvocationHandler {

    private BundleContext bundleContext;
    private ServiceReference reference;

    /**
     * Creates an instance of this class.
     *
     * @param bundleContext a {@link BundleContext} instance to get the service associated with the specified reference
     * @param reference a {@link ServiceReference} instance associated with the published OSGi service
     */
    public OsgiInvocationHandler(BundleContext bundleContext, ServiceReference reference) {
        this.bundleContext = bundleContext;
        this.reference = reference;
    }

    /**
     * Invokes the specified method on the exported OSGi service with the specified arguments.
     * <p/>
     * The algorithm to call the exported OSGi service the the following:<br/>
     * <ol>
     *     <li>If the method is {@link Object#equals(Object)} - identity equality is used to compare
     *     the proxy, the method is invoked on, with the passed arguments.</li>
     *     <li>If the method is {@link Object#hashCode()} - the value of {@link System#identityHashCode(Object)}
     *     invoked on the specified proxy is returned.</li>
     *     <li>If the method is {@link org.apache.camel.osgi.util.OsgiProxy#getReference()} - the
     *     {@link #getReference() service reference} is returned.</li>
     *     <li>If the method is provided by the {@link ServiceReference} - the call is delegated to the associated
     *     {@link #getReference() service reference}</li>
     *     <li>In all other cases the exported OSGi service is get, then the provided method is invoked on the gotten
     *     service and finally the service is released.</li>
     * </ol>
     *
     * @param proxy the proxy instance that the method was invoked on
     * @param method the <code>Method</code> instance corresponding to the interface method invoked on the
     * proxy instance
     * @param args args an array of objects containing the values of the arguments passed in the method invocation on
     * the proxy instance
     *
     * @return the value to return from the method invocation on the proxy instance
     *
     * @throws IllegalStateException if there is no service registered
     * @throws Throwable the exception to throw from the method invocation on the proxied instance
     */
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

    /**
     * Returns a {@link BundleContext} instance to get the service associated with the specified {@link ServiceReference}.
     *
     * @return a {@link BundleContext} instance
     */
    public BundleContext getBundleContext() {
        return bundleContext;
    }

    /**
     * Returns a {@link ServiceReference} instance associated with the published OSGi service.
     *
     * @return a {@link ServiceReference} instance
     */
    public ServiceReference getReference() {
        return reference;
    }
}

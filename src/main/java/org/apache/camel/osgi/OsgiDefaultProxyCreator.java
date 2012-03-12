package org.apache.camel.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

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

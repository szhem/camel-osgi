package org.apache.camel.osgi;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;


public class OsgiDefaultEndpoint extends DefaultEndpoint {

    private final BundleContext bundleContext;
    private final ClassLoader applicationClassLoader;
    private final ClassLoader bundleClassLoader;

    private Map<String, String> props = Collections.emptyMap();

    public OsgiDefaultEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
        this.bundleClassLoader = getClass().getClassLoader();

        ClassLoader classLoader = component.getCamelContext().getApplicationContextClassLoader();
        this.applicationClassLoader = classLoader;

        Bundle bundle;
        if(!(classLoader instanceof BundleReference)) {
            // try to resolve classloader through reflection if BundleReference has already been wrapped in the custom classloader
            // currently it works with spring-dm, aries, eclipse genimi, camel
            try {
                Method method = classLoader.getClass().getMethod("getBundle");
                bundle = (Bundle) method.invoke(classLoader);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        String.format("ClassLoader of CamelContext [%s] is not OSGi aware", classLoader));
            }
        } else {
            bundle = BundleReference.class.cast(classLoader).getBundle();
        }

        bundleContext = bundle.getBundleContext();
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new OsgiDefaultConsumer(this, processor, getProps());
    }

    @Override
    public Producer createProducer() throws Exception {
        return new OsgiDefaultProducer(this, getProps());
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public OsgiComponent getComponent() {
        return (OsgiComponent) super.getComponent();
    }

    public Map<String, String> getProps() {
        return props;
    }

    public void setProps(Map<String, String> props) {
        this.props = props;
    }

    protected BundleContext getBundleContext() {
        return bundleContext;
    }

    protected ClassLoader getApplicationClassLoader() {
        return applicationClassLoader;
    }

    protected ClassLoader getBundleClassLoader() {
        return bundleClassLoader;
    }
}

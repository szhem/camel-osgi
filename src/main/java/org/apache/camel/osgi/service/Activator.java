package org.apache.camel.osgi.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The {@code Activator} is the activator that stores {@link BundleContext} in the {@link AtomicReference} for the
 * later use.
 */
public class Activator implements BundleActivator {

    public static final AtomicReference<BundleContext> BUNDLE_CONTEXT = new AtomicReference<BundleContext>();

    @Override
    public void start(BundleContext context) throws Exception {
        BUNDLE_CONTEXT.set(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        BUNDLE_CONTEXT.set(null);
    }

}

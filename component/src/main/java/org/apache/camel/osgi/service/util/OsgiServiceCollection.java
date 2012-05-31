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

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OsgiServiceCollection<E> implements Collection<E> {

    protected final DynamicCollection<E> services;
    protected final Map<Long, E> idToService;

    protected final Object lock = new Object();

    protected final BundleContext bundleContext;

    protected final String filter;
    protected final ServiceListener listener;
    protected final ClassLoader fallbackClassLoader;
    protected final OsgiProxyCreator proxyCreator;

    /**
     * Create an instance of {@code OsgiServiceCollection}.
     *
     * @param bundleContext the {@link BundleContext} instance to look for the services that match the specified filter
     * @param filter OSGi instance to lookup OSGi services
     * @param fallbackClassLoader {@code ClassLoader} to load classes and resources in the case when these classes and
     * resources cannot be loaded by means of bundle associated with the given bundleContext
     * @param proxyCreator an instance of {@link OsgiProxyCreator} to wrap services registered in the OSGi registry
     */
    public OsgiServiceCollection(BundleContext bundleContext, String filter, ClassLoader fallbackClassLoader,
             OsgiProxyCreator proxyCreator) {
        this(bundleContext, filter, fallbackClassLoader, proxyCreator, new DynamicCollection<E>());
    }

    /**
     * Create an instance of {@code OsgiServiceCollection}.
     *
     * @param bundleContext the {@link BundleContext} instance to look for the services that match the specified filter
     * @param filter OSGi filter to lookup OSGi services
     * @param fallbackClassLoader {@code ClassLoader} to load classes and resources in the case when these classes and
     *    * resources cannot be loaded by means of bundle associated with the given bundleContext
     * @param proxyCreator an instance of {@link OsgiProxyCreator} to wrap services registered in the OSGi registry
     * @param backed the backed dynamic collection to hold proxies for OSGi services
     */
    public OsgiServiceCollection(BundleContext bundleContext, String filter, ClassLoader fallbackClassLoader,
             OsgiProxyCreator proxyCreator, DynamicCollection<E> backed) {
        this.bundleContext = bundleContext;
        this.filter = filter;
        this.fallbackClassLoader = fallbackClassLoader;
        this.proxyCreator = proxyCreator;
        this.services = backed;
        this.idToService = new HashMap<Long, E>();
        this.listener = new ServiceInstanceListener();
    }

    /**
     * Start tracking for OSGi services.
     *
     * @throws IllegalStateException if this collection was initialized with invalid OSGi filter
     */
    public void startTracking() {
        try {
            bundleContext.addServiceListener(listener, filter);
            ServiceReference[] alreadyDefined = bundleContext.getServiceReferences(null, filter);
            if(alreadyDefined != null) {
                for(ServiceReference ref : alreadyDefined) {
                    listener.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, ref));
                }
            }
        } catch (InvalidSyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Stops tracking for OSGi services releasing all obtained resource while starting tracking.
     */
    public void stopTracking() {
        bundleContext.removeServiceListener(listener);

        synchronized (lock) {
            for (E service : services) {
                listener.serviceChanged(new ServiceEvent(ServiceEvent.UNREGISTERING, ((OsgiProxy) service).getReference()));
            }
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new OsgiServiceIterator();
    }

    @Override
    public int size() {
        return services.size();
    }

    @Override
    public String toString() {
        return services.toString();
    }

    /*
        mutators are forbidden
    */

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        return services.contains(o);
    }

    @Override
    public boolean containsAll(Collection c) {
        return services.containsAll(c);
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Object[] toArray() {
        return services.toArray();
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    public <T> T[] toArray(T[] array) {
        return services.toArray(array);
    }

    protected class ServiceInstanceListener implements ServiceListener {
        @Override
        public void serviceChanged(ServiceEvent event) {
            ServiceReference ref = event.getServiceReference();
            Long serviceID = (Long) ref.getProperty(Constants.SERVICE_ID);
            switch (event.getType()) {
                case ServiceEvent.REGISTERED:
                case ServiceEvent.MODIFIED:
                    synchronized (lock) {
                        @SuppressWarnings("unchecked")
                        E service = (E) proxyCreator.createProxy(
                                bundleContext, ref, new BundleDelegatingClassLoader(ref.getBundle(), fallbackClassLoader));
                        idToService.put(serviceID, service);
                        services.add(service);
                    }
                    break;
                case ServiceEvent.UNREGISTERING:
                case ServiceEvent.MODIFIED_ENDMATCH:
                    synchronized (lock) {
                        E service = idToService.remove(serviceID);
                        if (service != null) {
                            services.remove(service);
                        }
                        bundleContext.ungetService(ref);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("unsupported event type: " + event);
            }
        }
    }

    protected class OsgiServiceIterator implements Iterator<E> {

        private final Iterator<E> iter = services.iterator();

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @SuppressWarnings("unchecked")
        public E next() {
            return iter.next();
        }

        @Override
        public void remove() {
            // mutators are forbidden
            throw new UnsupportedOperationException();
        }
    }

}
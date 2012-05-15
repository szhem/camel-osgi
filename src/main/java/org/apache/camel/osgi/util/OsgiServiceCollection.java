package org.apache.camel.osgi.util;

import org.apache.camel.RuntimeCamelException;
import org.apache.camel.osgi.OsgiProxy;
import org.apache.camel.osgi.OsgiProxyCreator;
import org.osgi.framework.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OsgiServiceCollection<E> implements Collection<E> {

	protected final DynamicCollection<E> services;
    protected final Map<Long, E> idToService;

    protected final Object lock = new Object();

    protected final BundleContext bundleContext;

    protected final Filter filter;
    protected final ServiceListener listener;
    protected final ClassLoader classLoader;
    protected final OsgiProxyCreator proxyCreator;

    /**
     *
     * @param bundleContext
     * @param filter
     * @param fallbackClassLoader {@code ClassLoader} to load classes and resources in the case when these classes and
*    * resources cannot be loaded by means of bundle associated with the given bundleContext
     * @param proxyCreator
     */
    public OsgiServiceCollection(BundleContext bundleContext, Filter filter, ClassLoader fallbackClassLoader,
             OsgiProxyCreator proxyCreator) {
        this(bundleContext, filter, fallbackClassLoader, proxyCreator, new DynamicCollection<E>());
    }

	public OsgiServiceCollection(BundleContext bundleContext, Filter filter, ClassLoader classLoader,
             OsgiProxyCreator proxyCreator, DynamicCollection<E> backed) {
        this.bundleContext = bundleContext;
		this.filter = filter;
        this.classLoader = classLoader;
        this.proxyCreator = proxyCreator;
        this.services = backed;
        this.idToService = new HashMap<Long, E>();
		this.listener = new ServiceInstanceListener();
	}

	public void startTracking() throws InterruptedException {
        try {
            String filter = this.filter.toString();

            bundleContext.addServiceListener(listener, filter);
            ServiceReference[] alreadyDefined = bundleContext.getServiceReferences(null, filter);
            if(alreadyDefined != null) {
                for(ServiceReference ref : alreadyDefined) {
                    listener.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, ref));
                }
            }
        } catch (InvalidSyntaxException e) {
            throw new RuntimeCamelException(e);
        }
    }

	public void stopTraking() {
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
                        E service = proxyCreator.createProxy(
                                bundleContext, ref, new BundleDelegatingClassLoader(ref.getBundle(), classLoader));
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
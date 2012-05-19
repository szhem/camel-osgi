package org.apache.camel.osgi.service.util;

import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistryFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ServiceLoader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class OsgiServiceCollectionTest {

    private PojoServiceRegistry registry;

    @Before
    public void setUp() throws Exception {
        ServiceLoader<PojoServiceRegistryFactory> loader = ServiceLoader.load(PojoServiceRegistryFactory.class);
        registry = loader.iterator().next().newPojoServiceRegistry(new HashMap<String, Object>());
    }

    private OsgiServiceCollection createCollection(String filter) throws Exception {
        OsgiProxyCreator proxyCreator = mock(OsgiProxyCreator.class);
        when(proxyCreator.createProxy(any(BundleContext.class), any(ServiceReference.class), any(ClassLoader.class)))
            .thenAnswer(new Answer<Object>() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    OsgiProxy osgiProxy = mock(OsgiProxy.class, withSettings().extraInterfaces(ServiceReference.class));
                    //return original service reference
                    when(osgiProxy.getReference()).thenReturn((ServiceReference) invocation.getArguments()[1]);
                    return osgiProxy;
                }
            });

        return new OsgiServiceCollection(registry.getBundleContext(), filter, getClass().getClassLoader(), proxyCreator);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testAdd() throws Exception {
        OsgiServiceCollection collection = createCollection("(a=b)");
        collection.startTracking();

        assertThat(collection.isEmpty(), equalTo(true));
        assertThat(collection.size(), equalTo(0));

        registry.registerService(
            Collection.class.getName(), new ArrayList<Object>(), new Hashtable(Collections.singletonMap("a", "b")));

        assertThat(collection.isEmpty(), equalTo(false));
        assertThat(collection.size(), equalTo(1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemove() throws Exception {
        OsgiServiceCollection collection = createCollection("(a=b)");
        collection.startTracking();

        ServiceRegistration registration = registry.registerService(
            Collection.class.getName(), new ArrayList<Object>(), new Hashtable(Collections.singletonMap("a", "b")));

        assertThat(collection.isEmpty(), equalTo(false));
        assertThat(collection.size(), equalTo(1));

        registration.unregister();

        assertThat(collection.isEmpty(), equalTo(true));
        assertThat(collection.size(), equalTo(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddNoMatch() throws Exception {
        OsgiServiceCollection collection = createCollection("(a=b)");
        collection.startTracking();

        assertThat(collection.isEmpty(), equalTo(true));
        assertThat(collection.size(), equalTo(0));

        registry.registerService(
            Collection.class.getName(), new ArrayList<Object>(), new Hashtable(Collections.singletonMap("c", "d")));

        assertThat(collection.isEmpty(), equalTo(true));
        assertThat(collection.size(), equalTo(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddWhileIterating() throws Exception {
        OsgiServiceCollection collection = createCollection("(a=b)");
        collection.startTracking();

        assertThat(collection.isEmpty(), equalTo(true));
        assertThat(collection.size(), equalTo(0));

        registry.registerService(
            Collection.class.getName(), new ArrayList<Object>(), new Hashtable(Collections.singletonMap("a", "b")));

        assertThat(collection.isEmpty(), equalTo(false));
        assertThat(collection.size(), equalTo(1));

        Iterator<?> iterator = collection.iterator();
        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), notNullValue());
        assertThat(iterator.hasNext(), equalTo(false));

        registry.registerService(
            Collection.class.getName(), new ArrayList<Object>(), new Hashtable(Collections.singletonMap("a", "b")));

        assertThat(collection.isEmpty(), equalTo(false));
        assertThat(collection.size(), equalTo(2));

        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), notNullValue());
        assertThat(iterator.hasNext(), equalTo(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveWhileIterating() throws Exception {
        OsgiServiceCollection collection = createCollection("(a=b)");
        collection.startTracking();

        registry.registerService(
            Collection.class.getName(), new ArrayList<Object>(), new Hashtable(Collections.singletonMap("a", "b")));
        ServiceRegistration registration = registry.registerService(
            Collection.class.getName(), new ArrayList<Object>(), new Hashtable(Collections.singletonMap("a", "b")));

        assertThat(collection.isEmpty(), equalTo(false));
        assertThat(collection.size(), equalTo(2));

        Iterator<?> iterator = collection.iterator();
        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), notNullValue());

        registration.unregister();

        assertThat(iterator.hasNext(), equalTo(false));
        assertThat(collection.isEmpty(), equalTo(false));
        assertThat(collection.size(), equalTo(1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStartTracking() throws Exception {
        OsgiServiceCollection collection = createCollection("(a=b)");

        assertThat(collection.isEmpty(), equalTo(true));
        assertThat(collection.size(), equalTo(0));

        registry.registerService(
            Collection.class.getName(), new ArrayList<Object>(), new Hashtable(Collections.singletonMap("a", "b")));

        assertThat(collection.isEmpty(), equalTo(true));
        assertThat(collection.size(), equalTo(0));

        collection.startTracking();

        assertThat(collection.isEmpty(), equalTo(false));
        assertThat(collection.size(), equalTo(1));

        registry.registerService(
            Collection.class.getName(), new ArrayList<Object>(), new Hashtable(Collections.singletonMap("a", "b")));

        assertThat(collection.isEmpty(), equalTo(false));
        assertThat(collection.size(), equalTo(2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStopTracking() throws Exception {
        OsgiServiceCollection collection = createCollection("(a=b)");
        collection.startTracking();

        registry.registerService(
            Collection.class.getName(), new ArrayList<Object>(), new Hashtable(Collections.singletonMap("a", "b")));

        assertThat(collection.isEmpty(), equalTo(false));
        assertThat(collection.size(), equalTo(1));

        collection.stopTracking();

        registry.registerService(
            Collection.class.getName(), new ArrayList<Object>(), new Hashtable(Collections.singletonMap("a", "b")));

        assertThat(collection.isEmpty(), equalTo(true));
        assertThat(collection.size(), equalTo(0));
    }

}

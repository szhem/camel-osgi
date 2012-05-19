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

public class OsgiServiceListTest {

    private PojoServiceRegistry registry;

    @Before
    public void setUp() throws Exception {
        ServiceLoader<PojoServiceRegistryFactory> loader = ServiceLoader.load(PojoServiceRegistryFactory.class);
        registry = loader.iterator().next().newPojoServiceRegistry(new HashMap<String, Object>());
    }

    private OsgiServiceList createList(String filter) throws Exception {
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

        return new OsgiServiceList(registry.getBundleContext(), filter, getClass().getClassLoader(), proxyCreator);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testAddDuplicates() throws Exception {
        OsgiServiceList list = createList("(a=b)");
        list.startTracking();

        assertThat(list.isEmpty(), equalTo(true));
        assertThat(list.size(), equalTo(0));

        ArrayList<Object> service = new ArrayList<Object>();
        registry.registerService(
            Collection.class.getName(), service, new Hashtable(Collections.singletonMap("a", "b")));
        registry.registerService(
            Collection.class.getName(), service, new Hashtable(Collections.singletonMap("a", "b")));

        assertThat(list.isEmpty(), equalTo(false));
        assertThat(list.size(), equalTo(2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveDuplicates() throws Exception {
        OsgiServiceList list = createList("(a=b)");
        list.startTracking();

        ArrayList<Object> service = new ArrayList<Object>();
        ServiceRegistration registration = registry.registerService(
            Collection.class.getName(), service, new Hashtable(Collections.singletonMap("a", "b")));
        registry.registerService(
            Collection.class.getName(), service, new Hashtable(Collections.singletonMap("a", "b")));

        assertThat(list.isEmpty(), equalTo(false));
        assertThat(list.size(), equalTo(2));

        registration.unregister();

        assertThat(list.isEmpty(), equalTo(false));
        assertThat(list.size(), equalTo(1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddWhileListIterating() throws Exception {
        OsgiServiceList list = createList("(a=b)");
        list.startTracking();

        assertThat(list.isEmpty(), equalTo(true));
        assertThat(list.size(), equalTo(0));

        registry.registerService(
            Collection.class.getName(), new ArrayList<Object>(), new Hashtable(Collections.singletonMap("a", "b")));

        assertThat(list.isEmpty(), equalTo(false));
        assertThat(list.size(), equalTo(1));

        Iterator<?> iterator = list.listIterator();
        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), notNullValue());
        assertThat(iterator.hasNext(), equalTo(false));

        registry.registerService(
            Collection.class.getName(), new ArrayList<Object>(), new Hashtable(Collections.singletonMap("a", "b")));

        assertThat(list.isEmpty(), equalTo(false));
        assertThat(list.size(), equalTo(2));

        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), notNullValue());
        assertThat(iterator.hasNext(), equalTo(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveWhileListIterating() throws Exception {
        OsgiServiceList list = createList("(a=b)");
        list.startTracking();

        registry.registerService(
            Collection.class.getName(), new ArrayList<Object>(), new Hashtable(Collections.singletonMap("a", "b")));
        ServiceRegistration registration = registry.registerService(
            Collection.class.getName(), new ArrayList<Object>(), new Hashtable(Collections.singletonMap("a", "b")));

        assertThat(list.isEmpty(), equalTo(false));
        assertThat(list.size(), equalTo(2));

        Iterator<?> iterator = list.listIterator();
        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), notNullValue());

        registration.unregister();

        assertThat(iterator.hasNext(), equalTo(false));
        assertThat(list.isEmpty(), equalTo(false));
        assertThat(list.size(), equalTo(1));
    }

}

package org.apache.camel.osgi;

import org.apache.camel.CamelContext;
import org.apache.camel.osgi.util.BundleDelegatingClassLoader;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.spi.Registry;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OsgiComponentTest {

    @Test
    public void testCreateEndpoint() throws Exception {
        Bundle bundle = mock(Bundle.class);
        BundleDelegatingClassLoader classLoader = new BundleDelegatingClassLoader(bundle, getClass().getClassLoader());

        Registry registry = mock(Registry.class);
        AggregationStrategy aggregationStrategy = mock(AggregationStrategy.class);
        when(registry.lookup(eq("aggregationStrategy"), eq(Object.class))).thenReturn(aggregationStrategy);

        CamelContext camelContext = mock(CamelContext.class);
        when(camelContext.getApplicationContextClassLoader()).thenReturn(classLoader);
        when(camelContext.getRegistry()).thenReturn(registry);

        OsgiComponent component = new OsgiComponent();
        component.setCamelContext(camelContext);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("parallelProcessing", true);
        params.put("streaming", true);
        params.put("aggregationStrategy", "#aggregationStrategy");
        params.put("prop1", "val1");
        params.put("prop2", 2);
        params.put("prop3", Boolean.TRUE);

        OsgiMulticastEndpoint endpoint =
                (OsgiMulticastEndpoint) component.createEndpoint("osgi:multicast:test", "multicast:test", params);
        assertThat(endpoint.isParallelProcessing(), equalTo(true));
        assertThat(endpoint.isStreaming(), equalTo(true));
        assertThat(endpoint.getAggregationStrategy(), sameInstance(aggregationStrategy));
        assertThat(params.isEmpty(), equalTo(true));

        Map<String, Object> props = endpoint.getProps();
        assertThat(props.size(), equalTo(5));
        assertThat((String) props.get("prop1"), equalTo("val1"));
        assertThat((Integer) props.get("prop2"), equalTo(2));
        assertThat((Boolean) props.get("prop3"), equalTo(Boolean.TRUE));
        assertThat((String) props.get(Constants.OBJECTCLASS), equalTo(OsgiComponent.OBJECT_CLASS));
        assertThat((String) props.get(OsgiComponent.SERVICE_NAME_PROP), equalTo("test"));
    }

}

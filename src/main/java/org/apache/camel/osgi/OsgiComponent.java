package org.apache.camel.osgi;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultComponent;
import org.osgi.framework.Constants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class OsgiComponent extends DefaultComponent {

    protected static final String SERVICE_NAME_ATTR = "camelOsgiEndpointName";
    protected static final String OBJECT_CLASS = Processor.class.getName();

    @Override
    protected Endpoint createEndpoint(String uri, String path, Map<String, Object> params) throws Exception {
        OsgiEndpointType endpointType = OsgiEndpointType.fromPath(path);
        OsgiDefaultEndpoint endpoint = endpointType.createEndpoint(uri, this);

        setProperties(endpoint, params);

        Map<String, String> props = new HashMap<String, String>();
        props.put(Constants.OBJECTCLASS, OBJECT_CLASS);
        props.put(SERVICE_NAME_ATTR, endpointType.getName(path));
        for(Iterator<Entry<String, Object>> iter = params.entrySet().iterator(); iter.hasNext(); ) {
            Entry<String, Object> entry = iter.next();
            props.put(entry.getKey(), String.valueOf(entry.getValue()));
            iter.remove();
        }
        endpoint.setProps(props);

        return endpoint;
    }

    @Override
    protected boolean useIntrospectionOnEndpoint() {
        return false;
    }
}

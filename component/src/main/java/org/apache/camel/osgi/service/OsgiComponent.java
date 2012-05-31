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

package org.apache.camel.osgi.service;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.TypeConverter;
import org.apache.camel.impl.DefaultComponent;
import org.osgi.framework.Constants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class OsgiComponent extends DefaultComponent {

    /**
     * OSGi service property that contains the name of the exposed into the OSGi registry camel processor.
     */
    protected static final String SERVICE_NAME_PROP = "camelOsgiEndpointName";

    /**
     * The value of the {@link Constants#OBJECTCLASS} property of the exposed into the OSGi registry camel processor.
     */
    protected static final String OBJECT_CLASS = Processor.class.getName();

    @Override
    protected Endpoint createEndpoint(String uri, String path, Map<String, Object> params) throws Exception {
        OsgiEndpointType endpointType = OsgiEndpointType.fromPath(path);
        OsgiDefaultEndpoint endpoint = endpointType.createEndpoint(uri, this);

        setProperties(endpoint, params);

        // properties that cannot be set on endpoint are exposed as published OSGi service
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(Constants.OBJECTCLASS, OBJECT_CLASS);
        props.put(SERVICE_NAME_PROP, endpointType.getName(path));
        for(Iterator<Entry<String, Object>> iter = params.entrySet().iterator(); iter.hasNext(); ) {
            Entry<String, Object> entry = iter.next();
            props.put(entry.getKey(), entry.getValue());
            iter.remove();
        }

        // convert some of predefined properties
        convertProperties(props);

        endpoint.setProps(props);

        return endpoint;
    }

    /**
     * Converts {@link Constants#SERVICE_ID} and {@link Constants#SERVICE_RANKING} if there are such in the provided
     * properties to the types defined by OSGi specification.
     * <p/>
     * {@link Constants#SERVICE_ID} will be of type {@code Long},
     * {@link Constants#SERVICE_RANKING} will be of type {@code Integer}
     *
     * @param props the properties to convert
     */
    protected void convertProperties(Map<String, Object> props) {
        // convert service id and ranking to the corresponding types
        if (props.containsKey(Constants.SERVICE_ID)) {
            props.put(Constants.SERVICE_ID, convertValue(props.get(Constants.SERVICE_ID), Long.class));
        }
        if (props.containsKey(Constants.SERVICE_RANKING)) {
            props.put(Constants.SERVICE_RANKING, convertValue(props.get(Constants.SERVICE_RANKING), Integer.class));
        }
    }

    /**
     * Converts value to the specified type.
     * <p/>
     * If the value can be converted to the target type then the converted value is returned, else the original value is
     * returned.
     *
     * @param value the value to convert
     * @param clazz the target type
     *
     * @return converted value
     */
    protected Object convertValue(Object value, Class<?> clazz) {
        TypeConverter converter = getCamelContext().getTypeConverter();
        
        Object answer = converter.convertTo(clazz, value);
        return answer != null ? answer : value;
    }
    
    @Override
    protected boolean useIntrospectionOnEndpoint() {
        return false;
    }
}

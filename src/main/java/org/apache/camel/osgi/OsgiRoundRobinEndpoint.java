package org.apache.camel.osgi;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;


public class OsgiRoundRobinEndpoint extends OsgiDefaultEndpoint {

    public OsgiRoundRobinEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException(
                String.format("[%s] does not support consumerns. Use [%s] instead.",
                        getClass().getName(), getClass().getSuperclass().getName()));
    }

    @Override
    public Producer createProducer() throws Exception {
        return new OsgiRoundRobinProducer(this, getProps());
    }

}

package org.apache.camel.osgi;

import org.apache.camel.Processor;
import org.apache.camel.processor.loadbalancer.RandomLoadBalancer;

import java.util.List;
import java.util.Map;

public class OsgiRandomProducer extends OsgiDefaultProducer {

    public OsgiRandomProducer(OsgiDefaultEndpoint endpoint, Map<String, String> props) {
        super(endpoint, props);
    }

    @Override
    protected Processor createProcessor() {
        return new RandomLoadBalancer() {
            @Override
            public List<Processor> getProcessors() {
                return services;
            }
        };
    }

}

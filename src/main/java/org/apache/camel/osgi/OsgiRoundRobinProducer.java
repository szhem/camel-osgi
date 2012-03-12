package org.apache.camel.osgi;

import org.apache.camel.Processor;
import org.apache.camel.processor.loadbalancer.RoundRobinLoadBalancer;

import java.util.List;
import java.util.Map;

public class OsgiRoundRobinProducer extends OsgiDefaultProducer {

    public OsgiRoundRobinProducer(OsgiDefaultEndpoint endpoint, Map<String, String> props) {
        super(endpoint, props);
    }

    @Override
    protected Processor createProcessor() {
        return new RoundRobinLoadBalancer() {
            @Override
            public List<Processor> getProcessors() {
                return services;
            }
        };
    }
}

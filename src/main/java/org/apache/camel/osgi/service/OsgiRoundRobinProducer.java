package org.apache.camel.osgi.service;

import org.apache.camel.Processor;
import org.apache.camel.processor.loadbalancer.RoundRobinLoadBalancer;

import java.util.List;
import java.util.Map;

/**
 * The {@code OsgiRoundRobinProducer} is the producer that uses {@link RoundRobinLoadBalancer} to send exchanges to OSGi
 * consumers.
 */
public class OsgiRoundRobinProducer extends OsgiDefaultProducer {

    public OsgiRoundRobinProducer(OsgiDefaultEndpoint endpoint, Map<String, Object> props) {
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

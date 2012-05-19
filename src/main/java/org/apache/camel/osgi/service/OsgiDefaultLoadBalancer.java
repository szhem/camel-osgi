package org.apache.camel.osgi.service;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.processor.loadbalancer.QueueLoadBalancer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The {@code OsgiDefaultLoadBalancer} is the load balancer that always selects most actual OSGi service, i.e. with
 * the highest ranking.
 * <p/>
 * As a rule processors to select from must implement {@link org.osgi.framework.ServiceReference} to work as expected.
 */
public class OsgiDefaultLoadBalancer extends QueueLoadBalancer {

    @Override
    protected synchronized Processor chooseProcessor(List<Processor> processors, Exchange exchange) {
        return choose(processors);
    }

    @SuppressWarnings("unchecked")
    private Processor choose(Collection<?> processors) {
        return (Processor) Collections.max((Collection<? extends Comparable>) processors);
    }
}

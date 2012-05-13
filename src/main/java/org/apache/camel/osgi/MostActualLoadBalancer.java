package org.apache.camel.osgi;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.processor.loadbalancer.QueueLoadBalancer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MostActualLoadBalancer extends QueueLoadBalancer {

    @Override
    protected synchronized Processor chooseProcessor(List<Processor> processors, Exchange exchange) {
        return choose(processors);
    }

    @SuppressWarnings("unchecked")
    private Processor choose(Collection<?> processors) {
        return (Processor) Collections.min((Collection<? extends Comparable>) processors);
    }
}

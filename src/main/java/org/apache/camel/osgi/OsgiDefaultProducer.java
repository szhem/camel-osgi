package org.apache.camel.osgi;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.osgi.filter.Filters;
import org.apache.camel.osgi.util.OsgiServiceList;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

import java.util.List;
import java.util.Map;

public class OsgiDefaultProducer extends DefaultProducer {

    protected final OsgiServiceList<Processor> services;
    protected Processor processor;

    public OsgiDefaultProducer(OsgiDefaultEndpoint endpoint, Map<String, String> props) {
        super(endpoint);
        try {
            this.services = new OsgiServiceList<Processor>(
                    endpoint.getBundleContext(),
                    FrameworkUtil.createFilter(Filters.allEq(props).value()),
                    endpoint.getBundleClassLoader(),
                    new OsgiDefaultProxyCreator());
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException(String.format("Unable to create filter from props [%s]", props), e);
        }
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        processor.process(exchange);
    }

    protected Processor createProcessor() {
        return new MostActualLoadBalancer() {
            @Override
            public List<Processor> getProcessors() {
                return services;
            }
        };
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        services.startTracking();
        if(processor == null) {
            processor = createProcessor();
        }
    }

    @Override
    protected void doStop() throws Exception {
        services.stopTraking();
        super.doStop();
    }
}


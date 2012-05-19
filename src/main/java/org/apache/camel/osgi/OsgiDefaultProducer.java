package org.apache.camel.osgi;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.osgi.filter.Filters;
import org.apache.camel.osgi.util.OsgiServiceList;
import org.apache.camel.util.ServiceHelper;

import java.util.List;
import java.util.Map;

public class OsgiDefaultProducer extends DefaultProducer {

    protected final OsgiServiceList<Processor> services;
    protected Processor processor;

    public OsgiDefaultProducer(OsgiDefaultEndpoint endpoint, Map<String, Object> props) {
        super(endpoint);
        this.services = new OsgiServiceList<Processor>(
            endpoint.getApplicationBundleContext(),
            Filters.allEq(props).filter(),
            endpoint.getComponentClassLoader(),
            new OsgiDefaultProxyCreator());
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        processor.process(exchange);
    }

    protected Processor createProcessor() {
        return new OsgiDefaultLoadBalancer() {
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
        ServiceHelper.startService(processor);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(processor);
        services.stopTraking();
        super.doStop();
    }

    @Override
    protected void doShutdown() throws Exception {
        ServiceHelper.stopAndShutdownService(processor);
        super.doShutdown();
    }
}


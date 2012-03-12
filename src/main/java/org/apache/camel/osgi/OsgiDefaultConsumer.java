package org.apache.camel.osgi;

import org.apache.camel.*;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.LoggingExceptionHandler;
import org.apache.camel.impl.ServiceSupport;
import org.apache.camel.spi.ExceptionHandler;
import org.apache.camel.util.ExchangeHelper;
import org.apache.camel.util.ServiceHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OsgiDefaultConsumer extends ServiceSupport implements Consumer, SuspendableService, Processor {

    private ServiceRegistration registration;
    private OsgiDefaultEndpoint endpoint;
    private Map<String, String> props;
    private Processor processor;
    private ExceptionHandler exceptionHandler;

    public OsgiDefaultConsumer(OsgiDefaultEndpoint endpoint, Processor processor, Map<String, String> props) {
        this.endpoint = endpoint;
        this.props = props;
        this.processor = processor;
    }

    @Override
    protected void doStart() throws Exception {
        ServiceHelper.startServices(processor);
        register();
    }

    @Override
    protected void doStop() throws Exception {
        unregister();
        ServiceHelper.stopServices(processor);
    }

    @Override
    protected void doResume() throws Exception {
        register();
    }

    @Override
    protected void doSuspend() throws Exception {
        unregister();
    }

    @Override
    public OsgiDefaultEndpoint getEndpoint() {
        return endpoint;
    }

    protected BundleContext getBundleContext() {
        return getEndpoint().getBundleContext();
    }

    protected void register() {
        if(this.registration == null) {
            this.registration = getBundleContext().registerService(
                    OsgiComponent.OBJECT_CLASS, this, new Hashtable<String, String>(props));
        }
    }

    protected void unregister() {
        if(this.registration != null) {
            this.registration.unregister();
            this.registration = null;
        }
    }

    @Override
    public void process(final Exchange exchange) throws Exception {
        Exchange copy = copyExchange(exchange);
        try {
            processor.process(copy);
        } finally {
            ExchangeHelper.copyResults(exchange, copy);
        }
    }

    protected Exchange copyExchange(Exchange exchange) {
        OsgiDefaultEndpoint endpoint = getEndpoint();

        DefaultExchange copy = new DefaultExchange(endpoint.getCamelContext(), exchange.getPattern());
        copy.setFromEndpoint(endpoint);
        copy.setProperty(Exchange.CORRELATION_ID, exchange.getExchangeId());

        if (exchange.hasProperties()) {
            copy.setProperties(new ConcurrentHashMap<String, Object>(exchange.getProperties()));
        }
        copy.getIn().copyFrom(exchange.getIn());
        if (exchange.hasOut()) {
            copy.getOut().copyFrom(exchange.getOut());
        }
        copy.setException(exchange.getException());
        return copy;
    }

    public ExceptionHandler getExceptionHandler() {
        if (exceptionHandler == null) {
            exceptionHandler = new LoggingExceptionHandler(getClass());
        }
        return exceptionHandler;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    protected void handleException(Throwable t) {
        Throwable newt = (t == null) ? new IllegalArgumentException("Handling [null] exception") : t;
        getExceptionHandler().handleException(newt);
    }
}

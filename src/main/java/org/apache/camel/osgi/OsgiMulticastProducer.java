package org.apache.camel.osgi;

import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.processor.MulticastProcessor;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.spi.ExecutorServiceManager;

import java.util.Map;
import java.util.concurrent.ExecutorService;

public class OsgiMulticastProducer extends OsgiDefaultProducer {

    private final AggregationStrategy aggregationStrategy;
    private final boolean parallelProcessing;
    private final ExecutorService executorService;
    private final boolean streaming;
    private final boolean stopOnExcetion;
    private final long timeout;
    private final Processor onPrepare;

    private final boolean defaultExecutorService;

    public OsgiMulticastProducer(OsgiDefaultEndpoint endpoint, Map<String, Object> props,
             AggregationStrategy aggregationStrategy, boolean parallelProcessing, ExecutorService executorService,
             boolean streaming, boolean stopOnExcetion, long timeout, Processor onPrepare) {
        super(endpoint, props);
        this.aggregationStrategy = aggregationStrategy;
        this.parallelProcessing = parallelProcessing;
        this.streaming = streaming;
        this.stopOnExcetion = stopOnExcetion;
        this.timeout = timeout;
        this.onPrepare = onPrepare;

        defaultExecutorService = parallelProcessing && executorService == null;
        if(defaultExecutorService) {
            CamelContext camelContext = endpoint.getCamelContext();
            ExecutorServiceManager manager = camelContext.getExecutorServiceManager();
            executorService = manager.newDefaultThreadPool(this, endpoint.getEndpointUri() + "(multicast)");
        }
        this.executorService = executorService;
    }

    @Override
    protected Processor createProcessor() {
        return new MulticastProcessor(getEndpoint().getCamelContext(), services, aggregationStrategy, parallelProcessing,
                executorService, streaming, stopOnExcetion, timeout, onPrepare, false);
    }

    @Override
    protected void doShutdown() throws Exception {
        if (defaultExecutorService) {
            getEndpoint().getCamelContext().getExecutorServiceManager().shutdownNow(executorService);
        }
        super.doShutdown();
    }
}

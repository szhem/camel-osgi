package org.apache.camel.osgi;

import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.processor.MulticastProcessor;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.spi.ExecutorServiceManager;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * The {@code OsgiMulticastProducer} is the producer that uses {@link MulticastProcessor} to send exchanges to OSGi
 * consumers.
 */
public class OsgiMulticastProducer extends OsgiDefaultProducer {

    private final AggregationStrategy aggregationStrategy;
    private final boolean parallelProcessing;
    private final ExecutorService executorService;
    private final boolean streaming;
    private final boolean stopOnException;
    private final long timeout;
    private final Processor onPrepare;

    private final boolean defaultExecutorService;

    public OsgiMulticastProducer(OsgiDefaultEndpoint endpoint, Map<String, Object> props,
             AggregationStrategy aggregationStrategy, boolean parallelProcessing, ExecutorService executorService,
             boolean streaming, boolean stopOnException, long timeout, Processor onPrepare) {
        super(endpoint, props);
        this.aggregationStrategy = aggregationStrategy;
        this.parallelProcessing = parallelProcessing;
        this.streaming = streaming;
        this.stopOnException = stopOnException;
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
                executorService, streaming, stopOnException, timeout, onPrepare, false);
    }

    @Override
    protected void doShutdown() throws Exception {
        if (defaultExecutorService) {
            getEndpoint().getCamelContext().getExecutorServiceManager().shutdownNow(executorService);
        }
        super.doShutdown();
    }

    public AggregationStrategy getAggregationStrategy() {
        return aggregationStrategy;
    }

    public boolean isParallelProcessing() {
        return parallelProcessing;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public boolean isStreaming() {
        return streaming;
    }

    public boolean isStopOnException() {
        return stopOnException;
    }

    public long getTimeout() {
        return timeout;
    }

    public Processor getOnPrepare() {
        return onPrepare;
    }

    public boolean isDefaultExecutorService() {
        return defaultExecutorService;
    }
}

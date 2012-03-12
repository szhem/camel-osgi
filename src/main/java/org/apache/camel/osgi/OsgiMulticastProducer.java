package org.apache.camel.osgi;

import org.apache.camel.Processor;
import org.apache.camel.processor.MulticastProcessor;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.Map;
import java.util.concurrent.ExecutorService;

public class OsgiMulticastProducer extends OsgiDefaultProducer {

    private AggregationStrategy aggregationStrategy;
    private boolean parallelProcessing;
    private ExecutorService executorService;
    private boolean streaming;
    private boolean stopOnExcetion;
    private long timeout;
    private Processor onPrepare;

    public OsgiMulticastProducer(OsgiDefaultEndpoint endpoint, Map<String, String> props,
             AggregationStrategy aggregationStrategy, boolean parallelProcessing, ExecutorService executorService,
             boolean streaming, boolean stopOnExcetion, long timeout, Processor onPrepare) {
        super(endpoint, props);
        this.aggregationStrategy = aggregationStrategy;
        this.parallelProcessing = parallelProcessing;
        this.executorService = executorService;
        this.streaming = streaming;
        this.stopOnExcetion = stopOnExcetion;
        this.timeout = timeout;
        this.onPrepare = onPrepare;
    }

    @Override
    protected Processor createProcessor() {
        return new MulticastProcessor(getEndpoint().getCamelContext(), services, aggregationStrategy, parallelProcessing,
                executorService, streaming, stopOnExcetion, timeout, onPrepare, false);
    }
}

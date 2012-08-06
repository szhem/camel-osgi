/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.camel.osgi.service;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.SuspendableService;
import org.apache.camel.support.ServiceSupport;
import org.apache.camel.util.ExchangeHelper;
import org.apache.camel.util.ServiceHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;
import java.util.Map;

/**
 * The {@code OsgiDefaultConsumer} is the default consumer for the camel OSGi component.
 * <p/>
 * It's responsible for publishing an appropriate OSGi service that will be available for different types of OSGi
 * producers.
 *
 * @see OsgiDefaultProducer
 * @see OsgiRandomProducer
 * @see OsgiRoundRobinProducer
 * @see OsgiMulticastProducer
 */
public class OsgiDefaultConsumer extends ServiceSupport implements Consumer, SuspendableService, Processor {

    private ServiceRegistration registration;
    private OsgiDefaultEndpoint endpoint;
    private Map<String, Object> props;
    private Processor processor;

    public OsgiDefaultConsumer(OsgiDefaultEndpoint endpoint, Processor processor, Map<String, Object> props) {
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

    protected BundleContext getApplicationBundleContext() {
        return getEndpoint().getApplicationBundleContext();
    }

    protected void register() {
        if(this.registration == null) {
            this.registration = getApplicationBundleContext().registerService(
                OsgiComponent.OBJECT_CLASS, this, new Hashtable<String, Object>(props));
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

    /**
     * Creates exchange copy, so that {@link org.apache.camel.Exchange#getContext()} will return the
     * {@code CamelContext} of the this consumer endpoint instead of the producer endpoint, that sent the provided
     * exchange.
     *
     * @param exchange an exchange to copy
     *
     * @return exchange copy
     */
    protected Exchange copyExchange(Exchange exchange) {
        OsgiDefaultEndpoint endpoint = getEndpoint();

        Exchange copy = ExchangeHelper.copyExchangeAndSetCamelContext(exchange, endpoint.getCamelContext(), false);
        copy.setFromEndpoint(endpoint);
        return copy;
    }

}

package org.apache.camel.osgi.service;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.osgi.service.OsgiDefaultLoadBalancer;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class OsgiDefaultLoadBalancerTest {

    @Test
    public void testChooseProcessorAmongEqual() throws Exception {
        Exchange exchange = mock(Exchange.class);

        List<Processor> processors = Arrays.asList(
            createProcessor(0, 0),
            createProcessor(0, 0)
        );

        OsgiDefaultLoadBalancer balancer = new OsgiDefaultLoadBalancer();
        assertThat(balancer.chooseProcessor(processors, exchange), sameInstance(processors.get(0)));
    }

    @Test
    public void testChooseProcessorByRanking() throws Exception {
        Exchange exchange = mock(Exchange.class);

        List<Processor> processors = Arrays.asList(
            createProcessor(0, 0),
            createProcessor(1, 1)
        );

        OsgiDefaultLoadBalancer balancer = new OsgiDefaultLoadBalancer();
        assertThat(balancer.chooseProcessor(processors, exchange), sameInstance(processors.get(1)));
    }

    @Test
    public void testChooseProcessorById() throws Exception {
        Exchange exchange = mock(Exchange.class);

        List<Processor> processors = Arrays.asList(
            createProcessor(2, 0),
            createProcessor(1, 0)
        );

        OsgiDefaultLoadBalancer balancer = new OsgiDefaultLoadBalancer();
        assertThat(balancer.chooseProcessor(processors, exchange), sameInstance(processors.get(1)));
    }

    private Processor createProcessor(long id, int ranking) {
        final ServiceReference service = mock(ServiceReference.class, withSettings().extraInterfaces(Processor.class));
        when(service.getProperty(Constants.SERVICE_ID)).thenReturn(id);
        when(service.getProperty(Constants.SERVICE_RANKING)).thenReturn(ranking);
        when(service.compareTo(any(ServiceReference.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ServiceReference other = (ServiceReference) invocation.getArguments()[0];

                Long id = (Long) service.getProperty(Constants.SERVICE_ID);
                Long otherId = (Long) other.getProperty(Constants.SERVICE_ID);

                if (id.equals(otherId)) {
                    return 0;
                }

                Integer rank = (Integer) service.getProperty(Constants.SERVICE_RANKING);
                Integer otherRank = (Integer) other.getProperty(Constants.SERVICE_RANKING);

                int answer = rank.compareTo(otherRank);
                if (answer == 0) {
                    answer = -id.compareTo(otherId);
                }

                return answer;
            }
        });
        
        return (Processor) service;
    }
}

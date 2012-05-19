## Apache Camel OSGi Services Component

1. [What is it?](#what-is-it)
2. [How to use?](#how-to-use)
 
### What is it?
This is [Apache Camel](http://camel.apache.org/) component that provides different ways of communication between 
camel contexts which are located in different OSGi bundles.

Currently 4 ways of communication are supported: 
[default](#default-way-of-communication-between-osgi-bundles), 
[multicast](#multicasting-to-multiple-osgi-bundles), 
[roundrobin](#roundrobin-load-balancing-between-multiple-osgi-bundles), 
[random](#random-load-balancing-between-multiple-osgi-bundles).

The component allows equally well to communicate between endpoints that are located in the same camel context
in a single bundle, between endpoints that are located in different camel contexts in a single bundle, between 
endpoints that are located in different camel contexts in separate bundles.

### How to use?

#### Default way of communication between OSGi bundles

The default way of communication uses standard OSGi behavior to select a most actual endpoint, published into the
OSGi service registry, i.e. the service with the highest ranking.

In order to use such a communication define the route like the following one in the producing bundle:

    <?xml version="1.0" encoding="UTF-8"?>
    <blueprint
        xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="
            http://www.osgi.org/xmlns/blueprint/v1.0.0 http://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd
            http://camel.apache.org/schema/blueprint http://camel.apache.org/schema/blueprint/camel-blueprint.xsd
        ">
        <camelContext xmlns="http://camel.apache.org/schema/blueprint">
            <route>
                <from uri="direct:start" />
                <to uri="osgi:consumer" />
            </route>
        </camelContext>
    </blueprint>

In the consuming bundles define the routes like this:

Bundle1:

    <?xml version="1.0" encoding="UTF-8"?>
    <blueprint
        xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="
            http://www.osgi.org/xmlns/blueprint/v1.0.0 http://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd
            http://camel.apache.org/schema/blueprint http://camel.apache.org/schema/blueprint/camel-blueprint.xsd
        ">
        <camelContext xmlns="http://camel.apache.org/schema/blueprint">
            <route>
                <from uri="osgi:consumer?service.ranking=0" />
                <to uri="mock:finish" />
            </route>
        </camelContext>
    </blueprint>

Bundle2:

    <?xml version="1.0" encoding="UTF-8"?>
    <blueprint
        xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="
            http://www.osgi.org/xmlns/blueprint/v1.0.0 http://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd
            http://camel.apache.org/schema/blueprint http://camel.apache.org/schema/blueprint/camel-blueprint.xsd
        ">
        <camelContext xmlns="http://camel.apache.org/schema/blueprint">
            <route>
                <from uri="osgi:consumer?service.ranking=100" />
                <to uri="mock:finish" />
            </route>
        </camelContext>
    </blueprint>
    
In this case an exchange will be delivered to the endpoint with the highest ranking in the Bundle2.

#### Multicasting to multiple OSGi bundles

The component allows sending exchanges to multiple endpoints using `MulticastProcessor` providing pub-sub way of 
communication. The supported component parameters are identical to the parameters, supported by 
[MulticastProcessor](http://camel.apache.org/multicast.html). All other parameters will be used as properties to lookup
or export an endpoint as an OSGi service.

In order to use such a communication define the route like the following one in the producing bundle:

    <?xml version="1.0" encoding="UTF-8"?>
    <blueprint
        xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="
            http://www.osgi.org/xmlns/blueprint/v1.0.0 http://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd
            http://camel.apache.org/schema/blueprint http://camel.apache.org/schema/blueprint/camel-blueprint.xsd
        ">
        <camelContext xmlns="http://camel.apache.org/schema/blueprint">
            <route>
                <from uri="direct:start" />
                <to uri="osgi:multicast:consumer?aggregationStrategy=#aggregationStrategy" />
            </route>
        </camelContext>
        <bean id="aggregationStrategy" class="org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy" />
    </blueprint>

In the consuming bundles define the routes like this:

Bundle1:

    <?xml version="1.0" encoding="UTF-8"?>
    <blueprint
        xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="
            http://www.osgi.org/xmlns/blueprint/v1.0.0 http://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd
            http://camel.apache.org/schema/blueprint http://camel.apache.org/schema/blueprint/camel-blueprint.xsd
        ">
        <camelContext xmlns="http://camel.apache.org/schema/blueprint">
            <route>
                <from uri="osgi:consumer" />
                <to uri="mock:finish" />
            </route>
        </camelContext>
    </blueprint>

Bundle2:

    <?xml version="1.0" encoding="UTF-8"?>
    <blueprint
        xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="
            http://www.osgi.org/xmlns/blueprint/v1.0.0 http://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd
            http://camel.apache.org/schema/blueprint http://camel.apache.org/schema/blueprint/camel-blueprint.xsd
        ">
        <camelContext xmlns="http://camel.apache.org/schema/blueprint">
            <route>
                <from uri="osgi:consumer" />
                <to uri="mock:finish" />
            </route>
        </camelContext>
    </blueprint>

In this case the exchange will be delivered to all the published consumer endpoints.

#### Roundrobin load balancing between multiple OSGi bundles 

The component allows sending exchanges to a single endpoint using round robin style of communication in the same way
as [RoundRobinLoadBalancer](http://camel.apache.org/maven/current/camel-core/apidocs/org/apache/camel/processor/loadbalancer/RoundRobinLoadBalancer.html).

In order to use such a communication define the route like the following one in the producing bundle:

    <?xml version="1.0" encoding="UTF-8"?>
    <blueprint
        xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="
            http://www.osgi.org/xmlns/blueprint/v1.0.0 http://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd
            http://camel.apache.org/schema/blueprint http://camel.apache.org/schema/blueprint/camel-blueprint.xsd
        ">
        <camelContext xmlns="http://camel.apache.org/schema/blueprint">
            <route>
                <from uri="direct:start" />
                <to uri="osgi:roundrobin:consumer" />
            </route>
        </camelContext>
    </blueprint>

In the consuming bundles define the routes like this:

Bundle1:

    <?xml version="1.0" encoding="UTF-8"?>
    <blueprint
        xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="
            http://www.osgi.org/xmlns/blueprint/v1.0.0 http://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd
            http://camel.apache.org/schema/blueprint http://camel.apache.org/schema/blueprint/camel-blueprint.xsd
        ">
        <camelContext xmlns="http://camel.apache.org/schema/blueprint">
            <route>
                <from uri="osgi:consumer" />
                <to uri="mock:finish" />
            </route>
        </camelContext>
    </blueprint>

Bundle2:

    <?xml version="1.0" encoding="UTF-8"?>
    <blueprint
        xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="
            http://www.osgi.org/xmlns/blueprint/v1.0.0 http://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd
            http://camel.apache.org/schema/blueprint http://camel.apache.org/schema/blueprint/camel-blueprint.xsd
        ">
        <camelContext xmlns="http://camel.apache.org/schema/blueprint">
            <route>
                <from uri="osgi:consumer" />
                <to uri="mock:finish" />
            </route>
        </camelContext>
    </blueprint>

In this case the exchange will be delivered to endpoints in all bundles in roundrobin fashion.

#### Random load balancing between multiple OSGi bundles

The component allows sending exchanges to a single endpoint using random style of communication in the same way
as [RandomLoadBalancer](http://camel.apache.org/maven/current/camel-core/apidocs/org/apache/camel/processor/loadbalancer/RandomLoadBalancer.html).

In order to use such a communication define the route like the following one in the producing bundle:

    <?xml version="1.0" encoding="UTF-8"?>
    <blueprint
        xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="
            http://www.osgi.org/xmlns/blueprint/v1.0.0 http://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd
            http://camel.apache.org/schema/blueprint http://camel.apache.org/schema/blueprint/camel-blueprint.xsd
        ">
        <camelContext xmlns="http://camel.apache.org/schema/blueprint">
            <route>
                <from uri="direct:start" />
                <to uri="osgi:random:consumer" />
            </route>
        </camelContext>
    </blueprint>

In the consuming bundles define the routes like this:

Bundle1:

    <?xml version="1.0" encoding="UTF-8"?>
    <blueprint
        xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="
            http://www.osgi.org/xmlns/blueprint/v1.0.0 http://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd
            http://camel.apache.org/schema/blueprint http://camel.apache.org/schema/blueprint/camel-blueprint.xsd
        ">
        <camelContext xmlns="http://camel.apache.org/schema/blueprint">
            <route>
                <from uri="osgi:consumer" />
                <to uri="mock:finish" />
            </route>
        </camelContext>
    </blueprint>

Bundle2:

    <?xml version="1.0" encoding="UTF-8"?>
    <blueprint
        xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="
            http://www.osgi.org/xmlns/blueprint/v1.0.0 http://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd
            http://camel.apache.org/schema/blueprint http://camel.apache.org/schema/blueprint/camel-blueprint.xsd
        ">
        <camelContext xmlns="http://camel.apache.org/schema/blueprint">
            <route>
                <from uri="osgi:consumer" />
                <to uri="mock:finish" />
            </route>
        </camelContext>
    </blueprint>

In this case the exchange will be delivered to endpoints in all bundles in random fashion.

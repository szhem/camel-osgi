## Apache Camel OSGi Services Component

1. [What is it?](#what-is-it)
2. [How to use?](#how-to-use)
3. [License](#license)

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

#### Endpoint parameters

##### Producer parameters

As a rule parameters specified on the producing endpoint will be used to create OSGi filter that later will be used to
lookup consuming endpoints in the OSGi service registry, so it's possible to specify any kind of parameters, 
for instance in the following example

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
                <to uri="osgi:consumer?param1=value1&amp;param2=value2&amp;param3=value3" />
            </route>
        </camelContext>
    </blueprint>

OSGi filter `(&(param1=value1)(param2=value2)(param3=value3))` will be created to lookup all the consuming endpoints.

##### Consumer parameters

The parameters specified on the consuming endpoints will be used as OSGi service properties when publishing this
endpoint into the OSGi service registry. So in the following example

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
                <from uri="osgi:consumer?param1=value1&amp;param2=value2&amp;param3=value3" />
                <to uri="mock:finish" />
            </route>
        </camelContext>
    </blueprint>

the published OSGi service will have properties `param1` with value `value1`, `param2` with value `value2`, 
`param3` with value `value3`.

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
communication. 

**Predefined consumer parameters**

| Name                | Default | Description  |
| ------------------- | ------- | ------------ |
| aggregationStrategy |         | Refers to an [AggregationStrategy](http://camel.apache.org/maven/current/camel-core/apidocs/org/apache/camel/processor/aggregate/AggregationStrategy.html) to be used to assemble the replies from the consumers, into a single outgoing message. By default the last reply is used as the outgoing message. |
| parallelProcessing  | false   | If enables then sending messages to the multicasts occurs concurrently. Note the caller thread will still wait until all messages has been fully processed, before it continues. Its only the sending and processing the replies from the multicasts which happens concurrently. |
| executorService     |         | Refers to a custom [Thread Pool](http://camel.apache.org/threading-model.html) to be used for parallel processing. Notice if you set this option, then parallel processing is automatic implied, and you do not have to enable that option as well. |
| streaming           | false   | If enabled then Camel will process replies out-of-order, i.e. in the order they come back. If disabled, Camel will process replies in the same order as multicasted. |
| stopOnException     | false   | Whether or not to stop continue processing immediately when an exception occurred. If disable, then Camel will send the message to all multicasts regardless if one of them failed. You can deal with exceptions in the [AggregationStrategy](http://camel.apache.org/maven/current/camel-core/apidocs/org/apache/camel/processor/aggregate/AggregationStrategy.html) class where you have full control how to handle that. |
| timeout             |         | Sets a total timeout specified in millis. If the Multicast hasn't been able to send and process all replies within the given timeframe, then the timeout triggers and the Multicast breaks out and continues. |
| onPrepare           |         | Refers to a custom [Processor](http://camel.apache.org/processor.html) to prepare the copy of the [Exchange](http://camel.apache.org/exchange.html) each multicast will receive. This allows you to do any custom logic, such as deep-cloning the message payload if that's needed etc. |

Note, that these parameters will not be exported into the OSGi service registry as part of the consuming endpoint
publishing, but all the other parameters will be.

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

### License

The component is distributed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

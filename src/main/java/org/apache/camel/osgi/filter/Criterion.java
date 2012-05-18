package org.apache.camel.osgi.filter;

import org.osgi.framework.Filter;

public interface Criterion {

    Filter filter();
    String value();
}

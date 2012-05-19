package org.apache.camel.osgi.filter;

import org.osgi.framework.Filter;

/**
 * The {@code Criterion} represent OSGi filter criterion.
 */
public interface Criterion {

    /**
     * Converts this criterion to the OSGi filter.
     *
     * @return OSGi filter
     */
    Filter filter();

    /**
     * Returns string representation of this criterion.
     *
     * @return string representation
     */
    String value();
}

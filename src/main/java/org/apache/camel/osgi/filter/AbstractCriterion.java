package org.apache.camel.osgi.filter;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

/**
 * The {@code AbstractCriterion} implements commons functionality for all the OSGi {@link Criterion criterion}
 * implementations.
 */
public abstract class AbstractCriterion implements Criterion {

    public Filter filter() {
        String value = value();

        if(value == null || value.isEmpty()) {
            return null;
        }

        try {
            return FrameworkUtil.createFilter(value);
        } catch (InvalidSyntaxException e) {
            throw new CriterionException(e);
        }
    }
    
    @Override
    public String toString() {
        return value();
    }

}

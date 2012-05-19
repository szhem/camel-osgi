package org.apache.camel.osgi.service.filter;

import org.osgi.framework.Constants;

/**
 * The {@code IsCriterion} is the criterion in which the object, this criterion applies to, must be published into the
 * OSGi registry under a certain class.
 */
public class IsCriterion extends AbstractCriterion {

    private String type;

    public IsCriterion(Class<?> type) {
        this.type = type.getName();
    }

    public IsCriterion(String type) {
        this.type = type;
    }

    @Override
    public String value() {
        return "(" + Constants.OBJECTCLASS + "=" + type + ')';
    }

}

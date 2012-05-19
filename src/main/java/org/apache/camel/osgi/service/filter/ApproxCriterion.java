package org.apache.camel.osgi.service.filter;

/**
 * The {@code ApproxCriterion} is the criterion that represents approximation of the specified attribute to the
 * specified value.
 */
public class ApproxCriterion extends AbstractCriterion {

    private String attribute;
    private Object value;

    public ApproxCriterion(String attribute, Object value) {
        this.attribute = attribute;
        this.value = value;
    }

    @Override
    public String value() {
        return '(' + attribute + "~=" + value + ')';
    }

}

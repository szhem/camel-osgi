package org.apache.camel.osgi.service.filter;

/**
 * The {@code EqCriterion} is the criterion that represents equality of the specified attribute to the
 * specified value.
 */
public class EqCriterion extends AbstractCriterion {

    private String attribute;
    private Object value;

    public EqCriterion(String attribute, Object value) {
        this.attribute = attribute;
        this.value = value;
    }

    @Override
    public String value() {
        return '(' + attribute + '=' + value + ')';
    }

}

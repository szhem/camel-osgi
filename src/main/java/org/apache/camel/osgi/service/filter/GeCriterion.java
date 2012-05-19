package org.apache.camel.osgi.service.filter;

/**
 * The {@code GeCriterion} is the criterion in which specified attribute must be greater or equal to the
 * specified value.
 */
public class GeCriterion extends AbstractCriterion {

    private String attribute;
    private Object value;

    public GeCriterion(String attribute, Object value) {
        this.attribute = attribute;
        this.value = value;
    }

    @Override
    public String value() {
        return '(' + attribute + ">=" + value + ')';
    }

}

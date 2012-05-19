package org.apache.camel.osgi.service.filter;

/**
 * The {@code LeCriterion} is the criterion in which specified attribute must be less or equal to the
 * specified value.
 */
public class LeCriterion extends AbstractCriterion {

    private String attribute;
    private Object value;

    public LeCriterion(String attribute, Object value) {
        this.attribute = attribute;
        this.value = value;
    }

    @Override
    public String value() {
        return '(' + attribute + "<=" + value + ')';
    }

}

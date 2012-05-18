package org.apache.camel.osgi.filter;

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

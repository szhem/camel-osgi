package org.apache.camel.osgi.filter;

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

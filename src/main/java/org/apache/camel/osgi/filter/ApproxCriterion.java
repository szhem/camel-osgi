package org.apache.camel.osgi.filter;

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

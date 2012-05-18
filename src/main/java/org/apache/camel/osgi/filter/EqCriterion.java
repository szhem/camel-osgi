package org.apache.camel.osgi.filter;

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

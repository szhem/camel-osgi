package org.apache.camel.osgi.filter;

public class LeFilter extends AbstractFilter {

    private String attribute;
    private Object value;

    public LeFilter(String attribute, Object value) {
        this.attribute = attribute;
        this.value = value;
    }

    @Override
    public String value() {
        return '(' + attribute + "<=" + value + ')';
    }

}

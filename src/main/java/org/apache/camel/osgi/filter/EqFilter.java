package org.apache.camel.osgi.filter;

public class EqFilter extends AbstractFilter {

    private String attribute;
    private Object value;

    public EqFilter(String attribute, Object value) {
        this.attribute = attribute;
        this.value = value;
    }

    @Override
    public String value() {
        return '(' + attribute + '=' + value + ')';
    }

}

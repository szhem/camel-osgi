package org.apache.camel.osgi.filter;

public class GeFilter extends AbstractFilter {

    private String attribute;
    private Object value;

    public GeFilter(String attribute, Object value) {
        this.attribute = attribute;
        this.value = value;
    }

    @Override
    public String value() {
        return '(' + attribute + ">=" + value + ')';
    }

}

package org.apache.camel.osgi.filter;

public class ApproxFilter extends AbstractFilter {

    private String attribute;
    private Object value;

    public ApproxFilter(String attribute, Object value) {
        this.attribute = attribute;
        this.value = value;
    }

    @Override
    public String value() {
        return '(' + attribute + "~=" + value + ')';
    }

}

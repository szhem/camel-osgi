package org.apache.camel.osgi.filter;

public class ExistsFilter extends AbstractFilter {

    private String attribute;

    public ExistsFilter(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public String value() {
        return '(' + attribute + "=*)";
    }

}

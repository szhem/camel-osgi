package org.apache.camel.osgi.filter;

public class StringFilter extends AbstractFilter {

    private String filter;

    public StringFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public String value() {
        return filter;
    }

}

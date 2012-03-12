package org.apache.camel.osgi.filter;


public class NotFilter extends AbstractFilter {

    private Filter filter;

    public NotFilter(Filter f) {
        this.filter = f;
    }

    @Override
    public String value() {
        return "(!" + filter.value() + ')';
    }

}

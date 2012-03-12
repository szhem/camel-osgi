package org.apache.camel.osgi.filter;

public class IsFilter extends AbstractFilter {

    private Class<?> type;

    public IsFilter(Class<?> type) {
        this.type = type;
    }

    @Override
    public String value() {
        return "(objectClass=" + type.getName() + ')';
    }

}

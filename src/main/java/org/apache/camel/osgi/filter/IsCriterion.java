package org.apache.camel.osgi.filter;

public class IsCriterion extends AbstractCriterion {

    private Class<?> type;

    public IsCriterion(Class<?> type) {
        this.type = type;
    }

    @Override
    public String value() {
        return "(objectClass=" + type.getName() + ')';
    }

}

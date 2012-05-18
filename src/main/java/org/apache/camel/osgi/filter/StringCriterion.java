package org.apache.camel.osgi.filter;

public class StringCriterion extends AbstractCriterion {

    private String filter;

    public StringCriterion(String filter) {
        this.filter = filter;
    }

    @Override
    public String value() {
        return filter;
    }

}

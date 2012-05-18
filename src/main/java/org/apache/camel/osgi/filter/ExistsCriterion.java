package org.apache.camel.osgi.filter;

public class ExistsCriterion extends AbstractCriterion {

    private String attribute;

    public ExistsCriterion(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public String value() {
        return '(' + attribute + "=*)";
    }

}

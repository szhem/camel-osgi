package org.apache.camel.osgi.filter;

/**
 * The {@code ExistsCriterion} is the criterion that represents existence of the specified attribute.
 */
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

package org.apache.camel.osgi.service.filter;

/**
 * The {@code RawCriterion} is the criterion that is completely defined by the raw representation of the OSGi filter.
 * <p/>
 * Note: as a rule {@link org.apache.camel.osgi.service.filter.Criterion#value()} returns a value that can be used to create an
 * instance of this class.
 */
public class RawCriterion extends AbstractCriterion {

    private String filter;

    public RawCriterion(String filter) {
        this.filter = filter;
    }

    @Override
    public String value() {
        return filter;
    }

}

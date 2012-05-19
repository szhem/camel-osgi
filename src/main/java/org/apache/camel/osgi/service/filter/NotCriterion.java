package org.apache.camel.osgi.service.filter;

/**
 * The {@code NotCriterion} is the criterion that represents criterion opposite to the specified one.
 */
public class NotCriterion extends AbstractCriterion {

    private Criterion criterion;

    public NotCriterion(Criterion criterion) {
        this.criterion = criterion;
    }

    @Override
    public String value() {
        return "(!" + criterion.value() + ')';
    }

}

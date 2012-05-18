package org.apache.camel.osgi.filter;

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

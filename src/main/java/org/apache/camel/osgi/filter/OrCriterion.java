package org.apache.camel.osgi.filter;

import java.util.*;

public class OrCriterion extends AbstractCriterion {

    private List<Criterion> filters = Collections.emptyList();

    public OrCriterion(Criterion... criterions) {
        this(Arrays.asList(criterions));
    }

    public OrCriterion(Collection<? extends Criterion> criterions) {
        this.filters = new ArrayList<Criterion>(criterions);
    }

    @Override
    public String value() {
        if(filters.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder(32);
        builder.append("(|");
        for(Criterion f : filters) {
            builder.append(f.value());
        }
        builder.append(')');
        return builder.toString();
    }

}

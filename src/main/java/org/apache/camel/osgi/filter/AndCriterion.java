package org.apache.camel.osgi.filter;

import java.util.*;

public class AndCriterion extends AbstractCriterion {

    private List<Criterion> criterions = Collections.emptyList();

    public AndCriterion(Criterion... criterions) {
        this(Arrays.asList(criterions));
    }

    public AndCriterion(Collection<? extends Criterion> criterions) {
        this.criterions = new ArrayList<Criterion>(criterions);
    }

    @Override
    public String value() {
        if(criterions.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder(32);
        builder.append("(&");
        for(Criterion criterion : criterions) {
            builder.append(criterion.value());
        }
        builder.append(')');
        return builder.toString();
    }

}
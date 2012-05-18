package org.apache.camel.osgi.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class Filters {

    private Filters() {
        throw new UnsupportedOperationException();
    }

    public static Criterion and(Collection<? extends Criterion> f) {
        return new AndCriterion(f);
    }

    public static Criterion or(Collection<? extends Criterion> f) {
        return new OrCriterion(f);
    }

    public static Criterion and(Criterion... f) {
        return new AndCriterion(f);
    }

    public static Criterion or(Criterion... f) {
        return new OrCriterion(f);
    }

    public static Criterion not(Criterion f) {
        return new NotCriterion(f);
    }

    public static Criterion eq(String attr, Object val) {
        return new EqCriterion(attr, val);
    }

    public static Criterion allEq(Map<String, ?> vals) {
        List<Criterion> filters = new ArrayList<Criterion>(vals.size());
        for(Entry<String, ?> entry : vals.entrySet()) {
            filters.add(eq(entry.getKey(), entry.getValue()));
        }
        return and(filters);
    }

    public static Criterion anyEq(Map<String, ?> vals) {
        List<Criterion> filters = new ArrayList<Criterion>(vals.size());
        for(Entry<String, ?> entry : vals.entrySet()) {
            filters.add(eq(entry.getKey(), entry.getValue()));
        }
        return or(filters);
    }

    public static Criterion ne(String attr, Object val) {
        return not(eq(attr, val));
    }

    public static Criterion approx(String attr, Object val) {
        return new ApproxCriterion(attr, val);
    }

    public static Criterion ge(final String attr, final Object val) {
        return new GeCriterion(attr, val);
    }

    public static Criterion le(String attr, Object val) {
        return new LeCriterion(attr, val);
    }

    public static Criterion exists(String attr) {
        return new ExistsCriterion(attr);
    }

    public static Criterion is(Class<?> type) {
        return new IsCriterion(type);
    }

    public static Criterion like(String attr, Object val, LikeCriterion.MatchMode mode) {
        return new LikeCriterion(attr, val, mode);
    }

    public static Criterion like(String attr, Object val) {
        return new LikeCriterion(attr, val);
    }

    public static Criterion string(String filter) {
        return new StringCriterion(filter);
    }

}

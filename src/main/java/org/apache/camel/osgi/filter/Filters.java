package org.apache.camel.osgi.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.osgi.filter.LikeFilter.MatchMode;

public final class Filters {

    private Filters() {
        throw new UnsupportedOperationException();
    }

    public static Filter and(Collection<? extends Filter> f) {
        return new AndFilter(f);
    }

    public static Filter or(Collection<? extends Filter> f) {
        return new OrFilter(f);
    }

    public static Filter and(Filter... f) {
        return new AndFilter(f);
    }

    public static Filter or(Filter... f) {
        return new OrFilter(f);
    }

    public static Filter not(Filter f) {
        return new NotFilter(f);
    }

    public static Filter eq(String attr, Object val) {
        return new EqFilter(attr, val);
    }

    public static Filter allEq(Map<String, ?> vals) {
        List<Filter> filters = new ArrayList<Filter>(vals.size());
        for(Entry<String, ?> entry : vals.entrySet()) {
            filters.add(eq(entry.getKey(), entry.getValue()));
        }
        return and(filters);
    }

    public static Filter anyEq(Map<String, ?> vals) {
        List<Filter> filters = new ArrayList<Filter>(vals.size());
        for(Entry<String, ?> entry : vals.entrySet()) {
            filters.add(eq(entry.getKey(), entry.getValue()));
        }
        return or(filters);
    }

    public static Filter ne(String attr, Object val) {
        return not(eq(attr, val));
    }

    public static Filter approx(String attr, Object val) {
        return new ApproxFilter(attr, val);
    }

    public static Filter ge(final String attr, final Object val) {
        return new GeFilter(attr, val);
    }

    public static Filter le(String attr, Object val) {
        return new LeFilter(attr, val);
    }

    public static Filter exists(String attr) {
        return new ExistsFilter(attr);
    }

    public static Filter is(Class<?> type) {
        return new IsFilter(type);
    }

    public static Filter like(String attr, Object val, MatchMode mode) {
        return new LikeFilter(attr, val, mode);
    }

    public static Filter like(String attr, Object val) {
        return new LikeFilter(attr, val);
    }

    public static Filter string(String filter) {
        return new StringFilter(filter);
    }

}

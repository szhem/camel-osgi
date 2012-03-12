package org.apache.camel.osgi.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OrFilter extends AbstractFilter {

    private List<Filter> filters = Collections.emptyList();

    public OrFilter(Filter... r) {
        this(Arrays.asList(r));
    }

    public OrFilter(Collection<? extends Filter> filters) {
        this.filters = new ArrayList<Filter>(filters);
    }

    @Override
    public String value() {
        if(filters.isEmpty()) {
            return EMPTY;
        }

        StringBuilder builder = new StringBuilder(32);
        builder.append("(|");
        for(Filter f : filters) {
            builder.append(f.value());
        }
        builder.append(')');
        return builder.toString();
    }

}

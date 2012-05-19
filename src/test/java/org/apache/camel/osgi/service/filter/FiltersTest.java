package org.apache.camel.osgi.service.filter;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.camel.osgi.service.filter.Filters.allEq;
import static org.apache.camel.osgi.service.filter.Filters.and;
import static org.apache.camel.osgi.service.filter.Filters.anyEq;
import static org.apache.camel.osgi.service.filter.Filters.approx;
import static org.apache.camel.osgi.service.filter.Filters.eq;
import static org.apache.camel.osgi.service.filter.Filters.le;
import static org.apache.camel.osgi.service.filter.Filters.like;
import static org.apache.camel.osgi.service.filter.Filters.ne;
import static org.apache.camel.osgi.service.filter.Filters.not;
import static org.apache.camel.osgi.service.filter.Filters.raw;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class FiltersTest {

    @Test
    public void testAllEq() throws Exception {
        Map<String, String> attrs = new LinkedHashMap<String, String>();
        attrs.put("a", "b");
        attrs.put("c", "d");

        Criterion criterion = allEq(attrs);
        assertThat(criterion.value(), equalTo("(&(a=b)(c=d))"));
        assertThat(criterion.filter(), notNullValue());
    }

    @Test
    public void testAnyEq() throws Exception {
        Map<String, String> attrs = new LinkedHashMap<String, String>();
        attrs.put("a", "b");
        attrs.put("c", "d");

        Criterion criterion = anyEq(attrs);
        assertThat(criterion.value(), equalTo("(|(a=b)(c=d))"));
        assertThat(criterion.filter(), notNullValue());
    }
    
    @Test
    public void testComplex() throws Exception {
        Map<String, String> attrs = new LinkedHashMap<String, String>();
        attrs.put("a", "b");
        attrs.put("c", "d");
        
        Criterion criterion = and(
            eq("a", "b"), 
            ne("b", "d"), 
            approx("g", "s"), 
            le("y", "z"), 
            raw("(&(g=n)(f=n))"),
            like("f", "g", LikeCriterion.MatchMode.ANYWHERE),
            not(anyEq(attrs))
        );

        assertThat(criterion.value(), equalTo("(&(a=b)(!(b=d))(g~=s)(y<=z)(&(g=n)(f=n))(f=*g*)(!(|(a=b)(c=d))))"));
        assertThat(criterion.filter(), notNullValue());

    }

}

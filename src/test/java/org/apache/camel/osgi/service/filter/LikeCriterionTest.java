package org.apache.camel.osgi.service.filter;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class LikeCriterionTest {
    
    @Test
    public void testCriterion() throws Exception {
        LikeCriterion criterion = new LikeCriterion("a", "b");
        assertThat(criterion.value(), equalTo("(a=b)"));
        assertThat(criterion.filter(), notNullValue());

        criterion = new LikeCriterion("a", "b", LikeCriterion.MatchMode.START);
        assertThat(criterion.value(), equalTo("(a=b*)"));
        assertThat(criterion.filter(), notNullValue());

        criterion = new LikeCriterion("a", "b", LikeCriterion.MatchMode.END);
        assertThat(criterion.value(), equalTo("(a=*b)"));
        assertThat(criterion.filter(), notNullValue());

        criterion = new LikeCriterion("a", "b", LikeCriterion.MatchMode.ANYWHERE);
        assertThat(criterion.value(), equalTo("(a=*b*)"));
        assertThat(criterion.filter(), notNullValue());

        criterion = new LikeCriterion("a", "b", LikeCriterion.MatchMode.EXACT);
        assertThat(criterion.value(), equalTo("(a=b)"));
        assertThat(criterion.filter(), notNullValue());
    }

}

package org.apache.camel.osgi.filter;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class AndCriterionTest {

    @Test
    public void testCriterion() throws Exception {
        AndCriterion criterion = new AndCriterion(new RawCriterion("(a=b)"), new RawCriterion("(c=d)"));

        assertThat(criterion.value(), equalTo("(&(a=b)(c=d))"));
        assertThat(criterion.filter(), notNullValue());
    }

    @Test
    public void testCriterionEmpty() throws Exception {
        AndCriterion criterion = new AndCriterion();

        assertThat(criterion.value(), equalTo(null));
        assertThat(criterion.filter(), nullValue());
    }

    @Test
    public void testCriterionOneOption() throws Exception {
        AndCriterion criterion = new AndCriterion(new RawCriterion("(a=b)"));

        assertThat(criterion.value(), equalTo("(&(a=b))"));
        assertThat(criterion.filter(), notNullValue());
    }

}

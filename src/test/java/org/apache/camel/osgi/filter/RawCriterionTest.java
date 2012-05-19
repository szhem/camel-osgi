package org.apache.camel.osgi.filter;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class RawCriterionTest {

    @Test
    public void testCriterion() throws Exception {
        RawCriterion criterion = new RawCriterion("(a=b)");

        assertThat(criterion.value(), equalTo("(a=b)"));
        assertThat(criterion.filter(), notNullValue());
    }

    @Test(expected = CriterionException.class)
    public void testCriterionInvalid() throws Exception {
        RawCriterion criterion = new RawCriterion("(a=b");

        assertThat(criterion.value(), equalTo("(a=b"));
        criterion.filter();
    }

}

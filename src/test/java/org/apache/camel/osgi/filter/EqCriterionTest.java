package org.apache.camel.osgi.filter;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class EqCriterionTest {

    @Test
    public void testCriterion() throws Exception {
        EqCriterion criterion = new EqCriterion("a", "b");
        assertThat(criterion.value(), equalTo("(a=b)"));
        assertThat(criterion.filter(), notNullValue());
    }

}

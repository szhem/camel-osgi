package org.apache.camel.osgi.service.filter;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class GeCriterionTest {

    @Test
    public void testCriterion() throws Exception {
        GeCriterion criterion = new GeCriterion("a", "b");
        assertThat(criterion.value(), equalTo("(a>=b)"));
        assertThat(criterion.filter(), notNullValue());
    }

}

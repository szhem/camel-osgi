package org.apache.camel.osgi.service.filter;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ExistsCriterionTest {

    @Test
    public void testCriterion() throws Exception {
        ExistsCriterion criterion = new ExistsCriterion("a");
        assertThat(criterion.value(), equalTo("(a=*)"));
        assertThat(criterion.filter(), notNullValue());
    }

}

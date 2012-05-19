package org.apache.camel.osgi.filter;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class NotCriterionTest {

    @Test
    public void testCriterion() throws Exception {
        NotCriterion criterion = new NotCriterion(new RawCriterion("(|(a=b)(c=d))"));

        assertThat(criterion.value(), equalTo("(!(|(a=b)(c=d)))"));
        assertThat(criterion.filter(), notNullValue());
    }

}

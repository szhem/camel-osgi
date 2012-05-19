package org.apache.camel.osgi.service.filter;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class IsCriterionTest {

    @Test
    public void testCriterion() throws Exception {
        IsCriterion criterion = new IsCriterion(List.class);
        assertThat(criterion.value(), equalTo("(objectClass=java.util.List)"));
        assertThat(criterion.filter(), notNullValue());
    }

}

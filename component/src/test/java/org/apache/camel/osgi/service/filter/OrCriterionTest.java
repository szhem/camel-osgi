/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.camel.osgi.service.filter;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class OrCriterionTest {

    @Test
    public void testCriterion() throws Exception {
        OrCriterion criterion = new OrCriterion(new RawCriterion("(a=b)"), new RawCriterion("(c=d)"));

        assertThat(criterion.value(), equalTo("(|(a=b)(c=d))"));
        assertThat(criterion.filter(), notNullValue());
    }

    @Test
    public void testCriterionEmpty() throws Exception {
        OrCriterion criterion = new OrCriterion();

        assertThat(criterion.value(), equalTo(null));
        assertThat(criterion.filter(), nullValue());
    }

    @Test
    public void testCriterionOneOption() throws Exception {
        OrCriterion criterion = new OrCriterion(new RawCriterion("(a=b)"));

        assertThat(criterion.value(), equalTo("(|(a=b))"));
        assertThat(criterion.filter(), notNullValue());
    }

}

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AndCriterion extends AbstractCriterion {

    private List<Criterion> criterions = Collections.emptyList();

    public AndCriterion(Criterion... criterions) {
        this(Arrays.asList(criterions));
    }

    public AndCriterion(Collection<? extends Criterion> criterions) {
        this.criterions = new ArrayList<Criterion>(criterions);
    }

    @Override
    public String value() {
        if(criterions.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder(32);
        builder.append("(&");
        for(Criterion criterion : criterions) {
            builder.append(criterion.value());
        }
        builder.append(')');
        return builder.toString();
    }

}

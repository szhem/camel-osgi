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

/**
 * The {@code RawCriterion} is the criterion that is completely defined by the raw representation of the OSGi filter.
 * <p/>
 * Note: as a rule {@link org.apache.camel.osgi.service.filter.Criterion#value()} returns a value that can be used to create an
 * instance of this class.
 */
public class RawCriterion extends AbstractCriterion {

    private String filter;

    public RawCriterion(String filter) {
        this.filter = filter;
    }

    @Override
    public String value() {
        return filter;
    }

}

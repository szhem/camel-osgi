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

package org.apache.camel.osgi.service;

import org.junit.Test;
import org.osgi.framework.BundleContext;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ActivatorTest {

    @Test
    public void testStart() throws Exception {
        BundleContext context = mock(BundleContext.class);

        Activator activator = new Activator();
        activator.start(context);

        assertThat(Activator.BUNDLE_CONTEXT.get(), notNullValue());
    }

    @Test
    public void testStop() throws Exception {
        BundleContext context = mock(BundleContext.class);

        Activator activator = new Activator();
        activator.start(context);
        activator.stop(context);

        assertThat(Activator.BUNDLE_CONTEXT.get(), nullValue());
    }

}

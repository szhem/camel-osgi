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

package org.apache.camel.osgi.service.util;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

public class DynamicCollectionTest {

    private static final Object OBJ_0 = new Object();
    private  static final Object OBJ_1 = new Object();

    private DynamicCollection<Object> collection;

    @Before
    public void setUp() {
        collection = new DynamicCollection<Object>();
    }

    @Test
    public void testAddRemove() throws Exception {
        assertThat(collection.size(), equalTo(0));
        assertThat(collection.isEmpty(), equalTo(true));

        collection.add(OBJ_0);
        assertThat(collection.size(), equalTo(1));
        assertThat(collection.isEmpty(), equalTo(false));

        assertThat(collection.contains(OBJ_0), equalTo(true));
        assertThat(collection.contains(OBJ_1), equalTo(false));

        collection.clear();
        assertThat(collection.size(), equalTo(0));

        collection.add(OBJ_0);
        collection.add(OBJ_0);

        assertThat(collection.size(), equalTo(2));

        assertThat(collection.remove(OBJ_0), equalTo(true));
        assertThat(collection.size(), equalTo(1));

        assertThat(collection.remove(OBJ_0), equalTo(true));
        assertThat(collection.size(), equalTo(0));
    }

    @Test
    public void testIterator() throws Exception {
        collection.add(OBJ_0);

        Iterator iterator = collection.iterator();
        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), sameInstance(OBJ_0));
        assertThat(iterator.hasNext(), equalTo(false));
    }

    @Test
    public void testAddWhileIterating() throws Exception {
        Iterator iterator = collection.iterator();
        assertThat(iterator.hasNext(), equalTo(false));

        collection.add(OBJ_0);
        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), sameInstance(OBJ_0));
        assertThat(iterator.hasNext(), equalTo(false));
    }

    @Test
    public void testRemoveElementWhileIterating() throws Exception {
        collection.add(OBJ_0);
        collection.add(OBJ_1);

        Iterator iterator = collection.iterator();
        assertThat(iterator.hasNext(), equalTo(true));
        collection.remove(OBJ_0);
        assertThat(iterator.next(), sameInstance(OBJ_0));
        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), sameInstance(OBJ_1));
        assertThat(iterator.hasNext(), equalTo(false));
    }

    @Test
    public void testRemoveElementAfterWhileIterating() throws Exception {
        collection.add(OBJ_0);
        collection.add(OBJ_1);

        Iterator iterator = collection.iterator();
        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), sameInstance(OBJ_0));
        collection.remove(OBJ_1);
        assertThat(iterator.hasNext(), equalTo(false));
    }

    @Test
    public void testRemoveElementBeforeWhileIterating() throws Exception {
        collection.add(OBJ_0);
        collection.add(OBJ_1);

        Iterator iterator = collection.iterator();
        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), sameInstance(OBJ_0));
        collection.remove(OBJ_0);
        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), sameInstance(OBJ_1));
        assertThat(iterator.hasNext(), equalTo(false));
    }

}

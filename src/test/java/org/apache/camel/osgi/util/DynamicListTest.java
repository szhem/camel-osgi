package org.apache.camel.osgi.util;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

public class DynamicListTest {

    private static final Object OBJ_0 = new Object();
    private  static final Object OBJ_1 = new Object();

    private DynamicList<Object> list;

    @Before
    public void setUp() {
        list = new DynamicList<Object>();
    }

    @Test
    public void testListIterator() throws Exception {
        list.add(OBJ_0);

        Iterator iterator = list.listIterator();
        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), sameInstance(OBJ_0));
        assertThat(iterator.hasNext(), equalTo(false));
    }

    @Test
    public void testAddWhileListIterating() throws Exception {
        Iterator iterator = list.listIterator();
        assertThat(iterator.hasNext(), equalTo(false));

        list.add(OBJ_0);
        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), sameInstance(OBJ_0));
        assertThat(iterator.hasNext(), equalTo(false));
    }

    @Test
    public void testRemoveWhileListIterating() throws Exception {
        list.add(OBJ_0);
        list.add(OBJ_1);

        Iterator iterator = list.listIterator();
        assertThat(iterator.hasNext(), equalTo(true));
        list.remove(OBJ_0);
        assertThat(iterator.next(), sameInstance(OBJ_0));
        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), sameInstance(OBJ_1));
        assertThat(iterator.hasNext(), equalTo(false));
    }

    @Test
    public void testRemoveAfterWhileListIterating() throws Exception {
        list.add(OBJ_0);
        list.add(OBJ_1);

        Iterator iterator = list.listIterator();
        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), sameInstance(OBJ_0));
        list.remove(OBJ_1);
        assertThat(iterator.hasNext(), equalTo(false));
    }

    @Test
    public void testRemoveBeforeWhileListIterating() throws Exception {
        list.add(OBJ_0);
        list.add(OBJ_1);

        Iterator iterator = list.iterator();
        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), sameInstance(OBJ_0));
        list.remove(OBJ_0);
        assertThat(iterator.hasNext(), equalTo(true));
        assertThat(iterator.next(), sameInstance(OBJ_1));
        assertThat(iterator.hasNext(), equalTo(false));
    }

}

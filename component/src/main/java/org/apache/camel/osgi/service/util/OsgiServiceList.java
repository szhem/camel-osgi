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

import org.osgi.framework.BundleContext;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * The {@code OsgiServiceList} is OSGi service dynamic list that allows iterating while the
 * underlying storage is being shrunk/expanded. This list is read-only as its content is being retrieved
 * dynamically from the OSGi platform.
 * <p/>
 * This list and its iterators are thread-safe. That is, multiple threads can access the list. However,
 * since the list is read-only, it cannot be modified by the client.
 */
public class OsgiServiceList<E> extends OsgiServiceCollection<E> implements List<E>, RandomAccess {

    public OsgiServiceList(BundleContext context, String filter, ClassLoader classLoader,
        OsgiProxyCreator proxyCreator) {

        super(context, filter, classLoader, proxyCreator, new DynamicList<E>());
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        return services.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return ((DynamicList<E>) services).indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return ((DynamicList<E>) services).lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        return new OsgiServiceListIterator(index);
    }

    // TODO: implement me (sublist must be backed by this list)
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    // mutators are forbidden

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E set(int index, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException();
    }

    protected class OsgiServiceListIterator implements ListIterator<E> {
        private final ListIterator<E> iter;

        @SuppressWarnings("unchecked")
        public OsgiServiceListIterator(int index) {
            iter = services.iterator(index);
        }

        @Override
        public E next() {
            return iter.next();
        }

        @Override
        public E previous() {
            return iter.previous();
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return iter.hasPrevious();
        }

        @Override
        public int nextIndex() {
            return iter.nextIndex();
        }

        @Override
        public int previousIndex() {
            return iter.previousIndex();
        }

        // mutators are forbidden

        @Override
        public void add(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(Object o) {
            throw new UnsupportedOperationException();
        }

    }

}
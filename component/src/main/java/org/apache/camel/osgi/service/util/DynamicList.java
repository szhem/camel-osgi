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

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * The {@code DynamicList} is implementation of {@link List} that allows iterators to see additions and
 * removals of elements while iterating.
 * <p/>
 * This list and its iterators are thread safe but all operations happen under a synchronization lock.
 */
public class DynamicList<E> extends DynamicCollection<E> implements List<E>, RandomAccess {

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        synchronized (lock) {
            int next = index;
            for (E element : c) {
                unsafeAdd(next++, element);
            }
            return next != index;
        }
    }

    @Override
    public E set(int index, E element) {
        if (element == null) {
            throw new NullPointerException();
        }
        synchronized (lock) {
            E prev = storage.get(index);
            storage.set(index, element);
            return prev;
        }
    }

    @Override
    public void add(int index, E element) {
        if (element == null) {
            throw new NullPointerException();
        }
        synchronized (lock) {
            unsafeAdd(index, element);
        }
    }

    @Override
    public int indexOf(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        synchronized (lock) {
            return storage.indexOf(o);
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        synchronized (lock) {
            return storage.lastIndexOf(o);
        }
    }

    public E get(int index) {
        synchronized (lock) {
            return storage.get(index);
        }
    }

    @Override
    public ListIterator<E> listIterator() {
        return iterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return iterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        synchronized (lock) {
            return storage.subList(fromIndex, toIndex);
        }
    }

}

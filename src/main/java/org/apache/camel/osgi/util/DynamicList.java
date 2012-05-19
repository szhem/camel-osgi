package org.apache.camel.osgi.util;

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

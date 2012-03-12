package org.apache.camel.osgi.util;

import org.apache.camel.osgi.OsgiProxyCreator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

public class OsgiServiceList<E> extends OsgiServiceCollection<E> implements List<E>, RandomAccess {

	public OsgiServiceList(BundleContext context, Filter filter, ClassLoader classLoader, OsgiProxyCreator proxyCreator) {
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

	//
	// WRITE operations forbidden
	//
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
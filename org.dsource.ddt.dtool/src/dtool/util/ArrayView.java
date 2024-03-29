package dtool.util;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import dtool.ast.IASTNeoNode;

/**
 * A simple immutable array collection (RandomAccess, Iterable). 
 */
public class ArrayView<E> implements Iterable<E>, RandomAccess, Collection<E> {
	
	public static <T> ArrayView<T> create(T[] arr){
		return new ArrayView<T>(arr);
	}
	
	protected final E[] array;
	
	public ArrayView(E[] array) {
		assertNotNull(array);
		this.array = array;
	}
	
	/** @return The *internal* array backing this {@link ArrayView}. Clients must not modify this array! */
	public final E[] getInternalArray() {
		return array;
	}
	
	/** @return this same array safely cast to a paremeterization with a super type parameter */
	@SuppressWarnings({ "unchecked" })
	public <U extends IASTNeoNode> ArrayView<U> upcast(Class<U> klass) {
		assertTrue(klass.isAssignableFrom(array.getClass())); // Unfortunately we can't do this check with generics,
		// see http://stackoverflow.com/questions/10809234/substitute-for-illegal-lower-bounds-on-a-generic-java-methodl
		return (ArrayView<U>) this;
	}
	
	@Override
	public final int size() {
		return array.length;
	}
	
	@Override
	public final boolean isEmpty() {
		return array.length == 0;
	}
	
	public final E get(int index) {
		return array[index];
	}
	
	@Override
	public final boolean contains(Object o) {
		return ArrayUtilExt.contains_v2(array, o);
	}
	
	@Override
	public final boolean containsAll(Collection<?> c) {
		return ArrayUtilExt.containsAll(this, c);
	}
	
	@Override
	public final Iterator<E> iterator() {
		return new ArrayIterator();
	}
	
	public final class ArrayIterator implements Iterator<E> {
		int index = 0;
		
		public ArrayIterator() { }
		
		@Override
		public boolean hasNext() {
			return index < array.length;
		}
		
		@Override
		public E next() throws NoSuchElementException {
			if (!hasNext())
				throw new NoSuchElementException();
			return array[index++];
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	@Override
	public final boolean add(E e) {
		throw assertFail();
	}
	
	@Override
	public final boolean remove(Object o) {
		throw assertFail();
	}
	
	@Override
	public final boolean addAll(Collection<? extends E> c) {
		throw assertFail();
	}
	
	@Override
	public final boolean removeAll(Collection<?> c) {
		throw assertFail();
	}
	
	@Override
	public final  boolean retainAll(Collection<?> c) {
		throw assertFail();
	}
	
	@Override
	public final void clear() {
		assertFail();
	}
	
	@Override
	public final Object[] toArray() {
		return Arrays.copyOf(array, array.length);
	}
	
	@Override
	public final <T> T[] toArray(T[] a) {
		return ArrayUtilExt.toArray(array, a);
	}
	
}
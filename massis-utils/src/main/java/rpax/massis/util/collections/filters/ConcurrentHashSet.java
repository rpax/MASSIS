package rpax.massis.util.collections.filters;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E> {

	private static final Object PRESENT = new Object();
	private final ConcurrentHashMap<E, Object> map;

	public ConcurrentHashSet() {
		this.map = new ConcurrentHashMap<E, Object>();
	}

	public ConcurrentHashSet(Collection<? extends E> c) {
		map = new ConcurrentHashMap<>(Math.max((int) (c.size() / .75f) + 1, 16));
		addAll(c);
	}

	public ConcurrentHashSet(int initialCapacity, float loadFactor) {
		map = new ConcurrentHashMap<>(initialCapacity, loadFactor);
	}

	public ConcurrentHashSet(int initialCapacity) {
		map = new ConcurrentHashMap<>(initialCapacity);
	}

	@Override
	public int size() {
		return this.map.size();
	}

	@Override
	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return this.map.containsKey(o);
	}

	@Override
	public Iterator<E> iterator() {
		return this.map.keySet().iterator();
	}

	@Override
	public Object[] toArray() {
		return this.map.keySet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.map.keySet().toArray(a);
	}

	@Override
	public boolean add(E e) {
		return this.map.put(e, PRESENT) != null;
	}

	@Override
	public boolean remove(Object o) {
		return this.map.remove(o) != null;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object object : c)
		{
			if (!map.containsKey(object))
				return false;
		}
		return true;
	}

	@Override
	public void clear() {
		this.map.clear();
	}

}

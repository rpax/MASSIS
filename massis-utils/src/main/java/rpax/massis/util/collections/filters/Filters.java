package rpax.massis.util.collections.filters;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.iterators.FilterIterator;

public class Filters {

	// public FilterIterator(Iterator<E> iterator, Predicate<? super E>
	// predicate) {
	public static final <ClassToFilter extends E, E> Iterable<ClassToFilter> filter(
			final Iterable<E> iterable, final Class<ClassToFilter> c) {
		return new Iterable<ClassToFilter>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Iterator<ClassToFilter> iterator() {
				return new FilterIterator(iterable.iterator(),
						new Predicate<E>() {
							@Override
							public boolean evaluate(E arg0) {
								return (c.isInstance(arg0));
							};
						});
			};

		};
	}

	public static final <E> Iterable<E> allExcept(final Iterable<E> iterable,
			final E e) {
		return new Iterable<E>() {

			@Override
			public Iterator<E> iterator() {
				return new FilterIterator<E>(iterable.iterator(),
						new Predicate<E>() {
							@Override
							public boolean evaluate(E otro) {
								return e != otro;
							};
						});
			};

		};
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <ClassToFilter extends T, T> Iterator<ClassToFilter> getIteratorFrom(
			final Iterable<T> iterable, final Class<ClassToFilter> c) {

		return new FilterIterator(iterable.iterator(), new Predicate<T>() {
			@Override
			public boolean evaluate(T arg0) {
				return (c.isInstance(arg0));
			}
		});
	}

	public static void main(String[] args0) {
		List<A> list = Arrays.asList(new B(), new C(), new D(), new C(),
				new B(), new D());
		for (C b : Filters.filter(list, D.class))
		{
			System.out.println(b.getClass());
		}
	}
}

abstract class A {

}

class B extends A {

}

class C extends A {

}

class D extends C {

}

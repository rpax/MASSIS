package com.massisframework.massis.sim.ecs;

public interface EntityEdit<T extends SimulationComponent> {

	public T get();

	public EntityEdit<T> set(Consumer0<T> action);

	public <U> EntityEdit<T> set(Consumer1<T, U> consumer, U value);

	public <U1, U2> EntityEdit<T> set(Consumer2<T, U1, U2> consumer,
			U1 u, U2 u2);

	public <U1, U2, U3> EntityEdit<T> set(
			Consumer3<T, U1, U2, U3> consumer, U1 u, U2 u2, U3 u3);

	public <U1, U2, U3, U4> EntityEdit<T> set(
			Consumer4<T, U1, U2, U3, U4> consumer, U1 u, U2 u2, U3 u3, U4 u4);

	public <U1, U2, U3, U4, U5> EntityEdit<T> set(
			Consumer5<T, U1, U2, U3, U4, U5> consumer, U1 u, U2 u2, U3 u3,
			U4 u4, U5 u5);

	@FunctionalInterface
	public interface Consumer0<T> {
		void set(T t);
	}

	@FunctionalInterface
	public interface Consumer1<T, U1> {

		void set(T t, U1 u);
	}

	@FunctionalInterface
	public interface Consumer2<T, U1, U2> {

		void set(T t, U1 u, U2 u2);
	}

	@FunctionalInterface
	public interface Consumer3<T, U1, U2, U3> {

		void set(T t, U1 u, U2 u2, U3 u3);
	}

	@FunctionalInterface
	public interface Consumer4<T, U1, U2, U3, U4> {

		void set(T t, U1 u, U2 u2, U3 u3, U4 u4);
	}

	@FunctionalInterface
	public interface Consumer5<T, U1, U2, U3, U4, U5> {

		void set(T t, U1 u, U2 u2, U3 u3, U4 u4, U5 u5);
	}
}

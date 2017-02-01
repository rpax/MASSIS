package com.massisframework.massis.sim.ecs.zayes;

public interface EntityEdit<T extends SimulationEntityComponent>
		extends Reseteable {

	public T get();

	public default EntityEdit<T> set(Consumer0<T> action)
	{
		action.set(get());
		return this;
	}

	public default <U> EntityEdit<T> set(Consumer1<T, U> consumer, U value)
	{
		consumer.set(get(), value);
		return this;
	}

	public default <U1, U2> EntityEdit<T> set(Consumer2<T, U1, U2> consumer,
			U1 u, U2 u2)
	{
		consumer.set(get(), u, u2);
		return this;
	}

	public default <U1, U2, U3> EntityEdit<T> set(
			Consumer3<T, U1, U2, U3> consumer, U1 u, U2 u2, U3 u3)
	{
		consumer.set(get(), u, u2, u3);
		return this;
	}

	public default <U1, U2, U3, U4> EntityEdit<T> set(
			Consumer4<T, U1, U2, U3, U4> consumer, U1 u, U2 u2, U3 u3, U4 u4)
	{
		consumer.set(get(), u, u2, u3, u4);
		return this;
	}

	public default <U1, U2, U3, U4, U5> EntityEdit<T> set(
			Consumer5<T, U1, U2, U3, U4, U5> consumer, U1 u, U2 u2, U3 u3,
			U4 u4, U5 u5)
	{
		consumer.set(get(), u, u2, u3, u4, u5);
		return this;
	}

	public SimulationEntity commit();

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

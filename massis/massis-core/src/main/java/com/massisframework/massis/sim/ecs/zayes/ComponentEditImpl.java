package com.massisframework.massis.sim.ecs.zayes;

import java.lang.ref.WeakReference;

import com.massisframework.massis.sim.ecs.ComponentEdit;
import com.massisframework.massis.sim.ecs.SimulationComponent;

class ComponentEditImpl<T extends SimulationComponent>
		implements ComponentEdit<T> {

	private WeakReference<DefaultInterfaceEntity> entityRef;
	private SimulationComponent component;

	public ComponentEditImpl(DefaultInterfaceEntity entity)
	{
		this.entityRef = new WeakReference<>(entity);
	}

	/**
	 * Must be called each time that we want to edit a component
	 * 
	 * @param component
	 *            the component
	 */
	public void setComponent(SimulationComponent component)
	{
		this.component = component;
	}

	public ComponentEdit<T> set(Consumer0<T> action)
	{
		action.set(get());
		update();
		return this;
	}

	public <U> ComponentEdit<T> set(Consumer1<T, U> consumer, U value)
	{
		consumer.set(get(), value);
		update();
		return this;
	}

	public <U1, U2> ComponentEdit<T> set(Consumer2<T, U1, U2> consumer,
			U1 u, U2 u2)
	{
		consumer.set(get(), u, u2);
		update();
		return this;
	}

	public <U1, U2, U3> ComponentEdit<T> set(
			Consumer3<T, U1, U2, U3> consumer, U1 u, U2 u2, U3 u3)
	{
		consumer.set(get(), u, u2, u3);
		return this;
	}

	public <U1, U2, U3, U4> ComponentEdit<T> set(
			Consumer4<T, U1, U2, U3, U4> consumer, U1 u, U2 u2, U3 u3, U4 u4)
	{
		consumer.set(get(), u, u2, u3, u4);
		update();
		return this;
	}

	public <U1, U2, U3, U4, U5> ComponentEdit<T> set(
			Consumer5<T, U1, U2, U3, U4, U5> consumer, U1 u, U2 u2, U3 u3,
			U4 u4, U5 u5)
	{
		consumer.set(get(), u, u2, u3, u4, u5);
		update();
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get()
	{
		return (T) this.component;
	}

	protected DefaultInterfaceEntity getEntity()
	{
		return this.entityRef.get();
	}

	private void update()
	{
		this.getEntity().set(this.component);
	}

}

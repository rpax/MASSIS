package com.massisframework.massis.sim.ecs.zayes;

import com.massisframework.massis.sim.ecs.ComponentEdit;
import com.massisframework.massis.sim.ecs.ComponentModifier;
import com.massisframework.massis.sim.ecs.SimulationComponent;

@SuppressWarnings({"rawtypes","unchecked"})
public class ComponentEditorAndModifier extends ComponentEditImpl
		implements ComponentModifier {

	public ComponentEditorAndModifier(DefaultInterfaceEntity entity)
	{
		super(entity);
	}

	@Override
	public <T extends SimulationComponent, K extends ComponentModifier & ComponentEdit<T>> K add(
			Class<T> type)
	{
		T cmp = this.getEntity().add_internal(type);
		this.setComponent(cmp);
		return (K) this;
	}

	@Override
	public <T extends SimulationComponent> ComponentModifier remove(
			Class<T> type)
	{
		this.getEntity().remove(type);
		return this;
	}

	@Override
	public <T extends SimulationComponent> ComponentEdit<T> edit(Class<T> type)
	{
		this.setComponent(this.getEntity().get(type));
		return this;
	}

}

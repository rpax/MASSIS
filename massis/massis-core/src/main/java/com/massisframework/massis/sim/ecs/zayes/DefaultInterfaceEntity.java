package com.massisframework.massis.sim.ecs.zayes;

import java.util.Arrays;

import com.massisframework.massis.sim.ecs.ComponentEdit;
import com.massisframework.massis.sim.ecs.ComponentModifier;
import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntity;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DefaultInterfaceEntity
		extends DefaultEntity
		implements SimulationEntity {

	private InterfaceEntityData ed;
	private SimulationComponent[] components;
	private Class<? extends SimulationComponent>[] types;

	public DefaultInterfaceEntity(
			InterfaceEntityData ed, EntityId id,
			SimulationComponent[] components,
			Class[] types)
	{
		super(ed, id, components, types);
		this.ed = ed;
		this.components = components;
		this.types = types;
		validate();
	}

	protected void validate()
	{
		if (types == null)
		{
			return;
		} else
		{
			super.validate();
		}
	}

	@Override
	public <T extends SimulationComponent, K extends ComponentModifier & ComponentEdit<T>> K add(
			Class<T> c)
	{
		T cmp = this.add_internal(c);
		ComponentEditorAndModifier editor = new ComponentEditorAndModifier(
				this);
		editor.setComponent(cmp);
		return (K) editor;
	}

	protected <T extends SimulationComponent> T add_internal(Class<T> c)
	{
		T cmp = get_internal(c);
		if (cmp == null)
		{
			cmp = this.ed.addNewComponent(this.getId(), c);
		}
		return cmp;
	}

	@Override
	public SimulationComponent get(Class c)
	{
		return get_internal(c);
	}

	@Override
	public <T extends SimulationComponent> ComponentEdit<T> edit(Class<T> type)
	{
		ComponentEditImpl<T> entityEdit = new ComponentEditImpl<>(this);
		entityEdit.setComponent(this.get_internal(type));
		return entityEdit;
	}

	private <T extends SimulationComponent> T get_internal(Class<T> c)
	{
		for (int i = 0; i < types.length; i++)
		{
			if (components[i] != null && c.isAssignableFrom(types[i]))
			{
				return c.cast(components[i]);
			}
		}
		return this.ed.simED.get(this.getId(), c);
	}

	@Override
	public <T extends SimulationComponent> ComponentModifier remove(
			Class<T> type)
	{
		this.ed.removeComponent(this.getId(), type);
		return this;
	}

	@Override
	public SimulationEntity getParent()
	{
		Long pId = this.get_internal(ParentComponent.class).getParentId();
		if (pId == null)
			return null;
		return this.ed.simED.getSimulationEntity(pId);
	}

	@Override
	public Iterable<SimulationEntity> getChildren()
	{
		return this.get_internal(ChildrenComponent.class)
				.getChildren()
				.stream()
				.map(this.ed.simED::getSimulationEntity)::iterator;
	}

	private void setRelationship(Long child, Long parent)
	{

		Long oldPId = this.ed.simED.getSimulationEntity(child)
				.get(ParentComponent.class)
				.getParentId();
		if (oldPId != null)
		{
			this.ed.simED
					.getSimulationEntity(oldPId)
					.edit(ChildrenComponent.class)
					.set(ChildrenComponent::remove, child);
		}
		if (parent != null)
		{
			this.ed.simED.getSimulationEntity(parent)
					.edit(ChildrenComponent.class)
					.set(ChildrenComponent::add, child);
		}
		this.ed.simED
				.getSimulationEntity(child)
				.edit(ParentComponent.class)
				.set(ParentComponent::setParentId, parent);
	}

	@Override
	public void addChild(long child)
	{
		setRelationship(child, this.id());
	}

	@Override
	public void setParent(Long parent)
	{
		setRelationship(this.id(), parent);
	}

	@Override
	public void setParent(SimulationEntity se)
	{
		this.setParent(se.id());
	}

	@Override
	public void removeFromParent()
	{
		this.setRelationship(this.id(), null);
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + "[" + id() + ", values="
				+ Arrays.asList(components) + "]";
	}
	// @Override
	// public SimulationComponent[] getAllComponents()
	// {
	// return this.components;
	// }

	@Override
	public long id()
	{
		return this.getId().getId();
	}

}

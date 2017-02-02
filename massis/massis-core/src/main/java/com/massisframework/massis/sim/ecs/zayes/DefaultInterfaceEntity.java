package com.massisframework.massis.sim.ecs.zayes;

import java.util.Arrays;

import com.massisframework.massis.sim.ecs.EntityEdit;
import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntity;

@SuppressWarnings({ "unchecked", "rawtypes" })
class DefaultInterfaceEntity extends DefaultEntity
		implements SimulationEntity {

	private InterfaceEntityData ed;
	private EntityId id;
	private SimulationComponent[] components;
	private Class<? extends SimulationComponent>[] types;

	public DefaultInterfaceEntity(
			InterfaceEntityData ed, EntityId id,
			SimulationComponent[] components,
			Class[] types)
	{
		super(ed, id, components, types);
		this.ed = ed;
		this.id = id;
		this.components = components;
		this.types = types;
		validate();
	}

	protected void validate()
	{
		if (types == null)
			return;
		else
		{
			for (int i = 0; i < types.length; i++)
			{
				if (components[i] == null)
				{
					continue;
				}
				if (components[i].getClass().isInstance(types[i]))
				{
					throw new RuntimeException(
							"Validation error.  components[" + i + "]:"
									+ components[i] + " is not of type:"
									+ types[i]);
				}
			}
		}
	}

	@Override
	public EntityId getId()
	{
		return id;
	}

	@Override
	public SimulationComponent[] getComponents()
	{
		return components;
	}

	// @Override
	// public <T extends EntityComponent> T get(Class<T> type)
	// {
	// for (EntityComponent c : components)
	// {
	// if (c != null && type.isAssignableFrom(c.getClass()))
	// {
	// return type.cast(c);
	// }
	// }
	// return ed.getComponent(id, type);
	// }

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultInterfaceEntity other = (DefaultInterfaceEntity) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public boolean isComplete()
	{
		for (int i = 0; i < components.length; i++)
		{
			if (components[i] == null)
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "Entity[" + id + ", values=" + Arrays.asList(components) + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public <T extends SimulationComponent> EntityEdit<T> addC(Class<T> c)
	{
		// ?¿
		T cmp = getComponent_internal(c);
		if (cmp == null)
		{
			cmp = this.ed.addNewComponent(id, c);
		}
		EntityEditImpl entityEdit = new EntityEditImpl<>(this);
		entityEdit.setComponent(cmp);
		return entityEdit;

	}

	@Override
	public <T extends SimulationComponent> T getC(Class<T> c)
	{
		return getComponent_internal(c);
	}

	@Override
	public <T extends SimulationComponent> EntityEdit<T> editC(
			Class<T> type)
	{
		EntityEditImpl<T> entityEdit = new EntityEditImpl<>(this);
		entityEdit.setComponent(this.getC(type));
		return entityEdit;
	}

	private <T extends SimulationComponent> T getComponent_internal(Class<T> c)
	{
		for (int i = 0; i < types.length; i++)
		{
			if (components[i] != null && c.isAssignableFrom(types[i]))
			{
				return c.cast(components[i]);
			}
		}
		return this.ed.getComponent(this.id, c);
	}

	@Override
	public <T extends SimulationComponent> void removeC(Class<T> type)
	{
		this.ed.removeComponent(id, type);
	}

	@Override
	public SimulationEntity getParent()
	{
		EntityId pId = this.getC(ParentComponent.class).getParentId();
		if (pId == null)
			return null;
		return this.ed.simED.getSimulationEntity(pId);
	}

	@Override
	public Iterable<SimulationEntity> getChildren()
	{
		return this.getC(ChildrenComponent.class)
				.getChildren()
				.stream()
				.map(this.ed.simED::getSimulationEntity)::iterator;
	}

	private void setRelationship(EntityId child, EntityId parent)
	{

		EntityId oldPId = this.ed.simED
				.getSimulationEntity(child)
				.getC(ParentComponent.class)
				.getParentId();
		if (oldPId != null)
		{
			this.ed.simED
					.getSimulationEntity(oldPId)
					.editC(ChildrenComponent.class)
					.set(ChildrenComponent::remove, child);
		}
		if (parent != null)
		{
			this.ed.simED.getSimulationEntity(parent)
					.editC(ChildrenComponent.class)
					.set(ChildrenComponent::add, child);
		}
		this.ed.simED
				.getSimulationEntity(child)
				.editC(ParentComponent.class)
				.set(ParentComponent::setParentId, parent);
	}

	@Override
	public void addChild(EntityId child)
	{
		setRelationship(child, this.id);
	}

	@Override
	public void setParent(EntityId parent)
	{
		setRelationship(this.id, parent);
	}

	@Override
	public void setParent(SimulationEntity se)
	{
		this.setParent(se.getId());
	}

	@Override
	public void removeFromParent()
	{
		this.setRelationship(this.id, null);
	}

}

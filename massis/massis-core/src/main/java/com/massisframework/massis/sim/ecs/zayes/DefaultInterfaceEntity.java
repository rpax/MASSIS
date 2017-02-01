package com.massisframework.massis.sim.ecs.zayes;

import java.util.Arrays;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntity;

public class DefaultInterfaceEntity extends DefaultEntity
		implements SimulationEntity {

	private InterfaceEntityData ed;
	private EntityId id;
	private SimulationComponent[] components;
	private static ThreadLocal<ObjectPool<EntityEditImpl>> entityEditPool_TL = ThreadLocal
			.withInitial(() -> {
				return ObjectPool.create(EntityEditImpl.class,
						() -> new EntityEditImpl());
			});
	private Class<? extends SimulationComponent>[] types; // temporarily for
															// validating
															// component types

	public DefaultInterfaceEntity(InterfaceEntityData ed, EntityId id,
			SimulationComponent[] components, Class[] types)
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
			cmp = this.ed.add(id, c);
		}
		return getEntityEdit(cmp);

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
		T cmp = this.getC(type);
		return getEntityEdit(cmp);
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
		return null;
	}

	@Override
	public <T extends SimulationComponent> void removeC(Class<T> type)
	{
		this.ed.removeComponent(id, type);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T extends SimulationComponent> EntityEdit<T> getEntityEdit(T cmp)
	{
		ObjectPool<EntityEditImpl> objectPool = entityEditPool_TL.get();
		EntityEditImpl<T> entityEdit = objectPool.get();
		entityEdit.setObjectPool(objectPool);
		entityEdit.setCmp(cmp);
		entityEdit.setEd(ed);
		entityEdit.setSe(this);
		return entityEdit;
	}

}

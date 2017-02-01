package com.massisframework.massis.sim.ecs.zayes;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Inject;
import com.simsilica.es.ComponentFilter;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.base.ComponentHandler;
import com.simsilica.es.base.DefaultEntityData;
import com.simsilica.es.base.DefaultEntitySet;

public class InterfaceEntityData extends DefaultEntityData
		implements SimulationEntityData {

	private InterfaceBindings bindings;
	private EntityComponentCreator componentCreator;

	private Map<Class<? extends SimulationComponent>, ObjectPool<? extends SimulationComponent>> componentPoolMap;

	@Inject
	public InterfaceEntityData(
			InterfaceBindings bindings,
			EntityComponentCreator componentCreator)
	{

		super();
		this.bindings = bindings;
		this.componentCreator = componentCreator;
		this.componentPoolMap = new ConcurrentHashMap<>();
		this.setUpPrivateFields();
	}

	@Override
	public SimulationEntitySet createEntitySet(Class... types)
	{
		return new EntitySetWrapper((InterfaceEntitySet) getEntities(types));
	}

	@Override
	public EntitySet getEntities(Class... types)
	{
		Class[] typesImpl = new Class[types.length];
		for (int i = 0; i < types.length; i++)
		{
			typesImpl[i] = getImplementingClass(types[i]);
		}
		EntitySet entitySet = super.getEntities(typesImpl);
		return entitySet;
	}

	private Class getImplementingClass(Class type)
	{
		return this.bindings.getBinding(type);
	}

	@Override
	protected InterfaceEntitySet createSet(ComponentFilter filter,
			Class... types)
	{
		InterfaceEntitySet set = new InterfaceEntitySet(this, filter, types);
		entitySets.add(set);
		return set;
	}

	@Override
	public Set<EntityId> findEntities(ComponentFilter filter, Class... types)
	{
		return super.findEntities(filter, types);
	}

	@Override
	protected ComponentHandler getHandler(Class type)
	{
		return super.getHandler(getImplementingClass(type));
	}
	////////////////////////////////////////////////

	private <K> K getFieldValue(String name)
	{
		try
		{
			Field f = DefaultEntityData.class.getDeclaredField(name);
			f.setAccessible(true);
			return (K) f.get(this);
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private Map<Class, ComponentHandler> handlers;
	private List<DefaultEntitySet> entitySets;

	private void setUpPrivateFields()
	{
		this.entitySets = getFieldValue("entitySets");
		this.handlers = getFieldValue("handlers");
	}

	@Override
	public <T extends SimulationComponent> T add(EntityId entityId,
			Class<T> component)
	{
		//
		T cmp = this.componentCreator.create(component);
		super.setComponent(entityId, cmp);
		return cmp;
	}

	@Override
	public <T extends SimulationComponent> T get(EntityId entityId,
			Class<T> type)
	{
		return super.getComponent(entityId, type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends SimulationComponent> void remove(EntityId entityId,
			Class<T> type)
	{
		T cmp = null;
		if (PooledObject.class.isAssignableFrom(type))
		{
			cmp = this.get(entityId, type);
		}
		super.removeComponent(entityId, type);
		if (cmp != null)
		{
			ObjectPool<T> pool = (ObjectPool<T>) this.componentPoolMap
					.get(cmp.getClass());
			if (pool == null)
			{
				pool = ObjectPool.create(type,
						() -> this.componentCreator.create(type),
						item -> ((PooledObject) item).reset());
			}
			pool.free(cmp);
		}
	}

}

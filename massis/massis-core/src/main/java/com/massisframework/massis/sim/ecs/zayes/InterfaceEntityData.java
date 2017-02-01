package com.massisframework.massis.sim.ecs.zayes;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashBiMap;
import com.google.inject.Inject;
import com.simsilica.es.ComponentFilter;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.base.ComponentHandler;
import com.simsilica.es.base.DefaultEntityData;
import com.simsilica.es.base.DefaultEntitySet;

public class InterfaceEntityData extends DefaultEntityData
		implements SimulationEntityData {

	private InterfaceBindings bindings;
	private EntityComponentCreator componentCreator;

	@Inject
	public InterfaceEntityData(
			InterfaceBindings bindings,
			EntityComponentCreator componentCreator)
	{

		super();
		this.bindings = bindings;
		this.componentCreator = componentCreator;
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
	protected InterfaceEntitySet createSet(ComponentFilter filter, Class... types)
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
	public <T extends SimulationEntityComponent> T add(EntityId entityId,
			Class<T> component)
	{
		//
		T cmp = this.componentCreator.create(component);
		super.setComponent(entityId, cmp);
		return cmp;
	}

	@Override
	public <T extends SimulationEntityComponent> T get(EntityId entityId,
			Class<T> type)
	{
		return super.getComponent(entityId, type);
	}

}

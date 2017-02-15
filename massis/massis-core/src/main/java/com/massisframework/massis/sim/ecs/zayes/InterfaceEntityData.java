package com.massisframework.massis.sim.ecs.zayes;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import com.massisframework.massis.sim.ecs.InterfaceBindings;
import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.simsilica.es.ComponentFilter;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.ComponentHandler;
import com.simsilica.es.base.DefaultEntityData;
import com.simsilica.es.base.DefaultEntitySet;

@SuppressWarnings({ "unchecked", "rawtypes" })
class InterfaceEntityData
		extends DefaultEntityData {

	private InterfaceBindings bindings;
	private List<DefaultEntitySet> entitySets;
	private Map<Class, ComponentHandler> _handlers;
	private SimulationEntityDataImpl simulationED;

	public InterfaceEntityData(
			InterfaceBindings bindings,
			SimulationEntityDataImpl simED)
	{
		super();
		this.simulationED = simED;
		this.bindings = bindings;
		this.entitySets = getFieldValue("entitySets");
		this._handlers = getFieldValue("handlers");
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
	public DefaultInterfaceEntity getEntity(EntityId entityId, Class... types)
	{
		SimulationComponent[] values = new SimulationComponent[types.length];
		for (int i = 0; i < values.length; i++)
		{
			values[i] = (SimulationComponent) getComponent(entityId, types[i]);
		}
		return new DefaultInterfaceEntity(this, entityId, values, types);
	}

	@Override
	protected ComponentHandler getHandler(Class type)
	{
		return super.getHandler(type);
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

	// public <T extends SimulationComponent> T addNewComponent(
	// long id, Class<T> c)
	// {
	// return this.simulationED.add(id, c).get();
	// }

	public <T extends SimulationComponent> T get(long id, Class<T> type)
	{
		return this.simulationED.get(id, type);
	}

	public SimulationEntity getSimulationEntity(long id)
	{
		return this.simulationED.getSimulationEntity(id);
	}

	protected SimulationEntityDataImpl getSimulationED()
	{
		return simulationED;
	}

//	@Override
//	protected Set<EntityId> getEntityIds(Class type)
//	{
//		// return getHandler(type).getEntities();
//		return new MultiHandlerSet(this, type, null);
//	}
//
//	@Override
//	protected Set<EntityId> getEntityIds(Class type, ComponentFilter filter)
//	{
//		return new MultiHandlerSet(this, type, filter);
//		// return getHandler(type).getEntities(filter);
//	}

	public Map<Class, ComponentHandler> getHandlers()
	{
		return _handlers;
	}

}

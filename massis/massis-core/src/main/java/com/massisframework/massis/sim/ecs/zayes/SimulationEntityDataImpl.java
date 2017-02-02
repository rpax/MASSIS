package com.massisframework.massis.sim.ecs.zayes;

import java.util.Map;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.impl.TransformImpl;
import com.massisframework.massis.sim.ecs.CollectionsFactory;
import com.massisframework.massis.sim.ecs.EntityComponentCreator;
import com.massisframework.massis.sim.ecs.ComponentEdit;
import com.massisframework.massis.sim.ecs.InterfaceBindings;
import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationEntityData;
import com.massisframework.massis.sim.ecs.SimulationEntitySet;
import com.simsilica.es.EntityId;

@SuppressWarnings({ "unchecked", "rawtypes" })
class SimulationEntityDataImpl implements SimulationEntityData {

	protected InterfaceEntityData ed;
	protected InterfaceBindings bindings;
	protected EntityComponentCreator componentCreator;
	private Map<Long, DefaultInterfaceEntity> allEntities;

	private static final Class[] DEFAULT_COMPONENTS = {
			TransformImpl.class,
			ChildrenComponent.class,
			ParentComponent.class
	};

	@Inject
	public SimulationEntityDataImpl(
			InterfaceBindings bindings,
			EntityComponentCreator componentCreator)
	{
		this.bindings = bindings;
		this.componentCreator = componentCreator;
		this.allEntities = CollectionsFactory.newMap(Long.class,
				DefaultInterfaceEntity.class);
		this.ed = new InterfaceEntityData(bindings, componentCreator, this);
	}

	@Override
	public void removeEntity(EntityId entityId)
	{
		// TODO detect parent children dependencies
		this.ed.removeEntity(entityId);
	}

	@Override
	public SimulationEntitySet createEntitySet(Class... types)
	{
		Class[] typesImpl = new Class[types.length];
		for (int i = 0; i < types.length; i++)
		{
			typesImpl[i] = this.bindings.getBinding(types[i]);
		}
		InterfaceEntitySet entitySet = (InterfaceEntitySet) ed
				.getEntities(typesImpl);
		return new EntitySetWrapper(entitySet);
	}

	@Override
	public SimulationEntity getSimulationEntity(EntityId id)
	{
		return this.getSimulationEntity(id.getId());
	}

	@Override
	public DefaultInterfaceEntity getSimulationEntity(long id)
	{
		return this.allEntities.get(id);
	}

	@Override
	public void close()
	{
		this.ed.close();
	}

	@Override
	public <T extends SimulationComponent> ComponentEdit<T> add(
			EntityId entityId,
			Class<T> component)
	{
		//
		T cmp = addGet(entityId, component);
		ComponentEditImpl<T> entityEdit = new ComponentEditImpl<>(
				getSimulationEntity(entityId.getId()));
		entityEdit.setComponent(cmp);
		return entityEdit;
	}

	@Override
	public SimulationEntity createEntity()
	{
		EntityId eid = ed.createEntity();

		for (int i = 0; i < DEFAULT_COMPONENTS.length; i++)
		{
			Class type = DEFAULT_COMPONENTS[i];
			this.ed.setComponent(eid, this.componentCreator.create(type));
		}
		DefaultInterfaceEntity entity = (DefaultInterfaceEntity) this.ed
				.getEntity(eid, DEFAULT_COMPONENTS);
		this.allEntities.put(eid.getId(), entity);
		return entity;
	}

	@Override
	public <T extends SimulationComponent> void remove(EntityId entityId,
			Class<T> type)
	{
		ed.removeComponent(entityId, type);
	}

	@Override
	public <T extends SimulationComponent> Iterable<SimulationEntity> findEntities(
			Class... types)
	{
		SimulationEntitySet es = this.createEntitySet(types);
		es.applyChanges();
		es.release();
		return es;
	}

	@Override
	public <T extends SimulationComponent> T addGet(
			EntityId entityId,
			Class<T> component)
	{
		T cmp = this.componentCreator.create(component);
		ed.setComponent(entityId, cmp);
		return cmp;
	}

	@Override
	public <T extends SimulationComponent> T get(EntityId entityId,
			Class<T> type)
	{
		return ed.getComponent(entityId, type);
	}

}

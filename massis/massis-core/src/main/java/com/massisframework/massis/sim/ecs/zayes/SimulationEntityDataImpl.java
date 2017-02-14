package com.massisframework.massis.sim.ecs.zayes;

import static com.massisframework.massis.sim.ecs.CollectionsFactory.newMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.impl.TransformImpl;
import com.massisframework.massis.sim.ecs.ComponentChangeListener;
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
	private Map<Long, DefaultInterfaceEntity> allEntities;
	private Map<Long, EntityId> entityIdMap;
	private List<ComponentChangeListener> componentChangeListeners;

	@Inject
	public SimulationEntityDataImpl(
			InterfaceBindings bindings)
	{
		this.bindings = bindings;
		this.allEntities = newMap(Long.class, DefaultInterfaceEntity.class);
		this.entityIdMap = newMap(Long.class, EntityId.class);
		this.ed = new InterfaceEntityData(bindings, this);
		this.componentChangeListeners = new CopyOnWriteArrayList<>();
	}

	private EntityId getMappedEntityId(long id)
	{
		EntityId eId = this.entityIdMap.get(id);
		if (eId == null)
		{
			eId = new EntityId(id);
			this.entityIdMap.put(id, eId);
		}
		return eId;
	}

	@Override
	public void removeEntity(long entityId)
	{
		this.ed.removeEntity(getMappedEntityId(entityId));
		this.allEntities.remove(entityId);
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
	public DefaultInterfaceEntity getSimulationEntity(long id)
	{
		return this.allEntities.get(id);
	}

	@Override
	public void close()
	{
		this.ed.close();
	}

	protected SimulationEntity getDefaultEntity(long id)
	{
		return this.allEntities.get(id);
	}

	@Override
	public <T extends SimulationComponent> void add(long entityId, T cmp)
	{
		this.allEntities.get(entityId).add(cmp);
		this.componentChangeListeners.forEach(l -> l.componentInserted(cmp));
	}

	@Override
	public <T extends SimulationComponent> void remove(long entityId,Class<T> type)
	{
		T cmp = ed.getComponent(getMappedEntityId(entityId), type);
		this.componentChangeListeners.forEach(l -> l.componentRemoved(cmp));
		ed.removeComponent(getMappedEntityId(entityId), type);
	}

	@Override
	public SimulationEntity createEntity()
	{
		EntityId eid = ed.createEntity();
		DefaultInterfaceEntity entity = createWithDefaultComponents(eid);
		this.allEntities.put(eid.getId(), entity);
		return entity;
	}

	protected DefaultInterfaceEntity createWithDefaultComponents(EntityId eid)
	{
		SimulationComponent[] defaultComponents = {
				new TransformImpl(),
				new ChildrenComponent(),
				new ParentComponent()
		};
		Class[] defaultComponentTypes = Arrays
				.stream(defaultComponents)
				.map(SimulationComponent::getClass)
				.toArray(s -> new Class[s]);
		for (int i = 0; i < defaultComponents.length; i++)
		{
			this.ed.setComponent(eid, defaultComponents[i]);
		}
		return this.ed.getEntity(eid, defaultComponentTypes);

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
	public <T extends SimulationComponent> T get(long entityId,
			Class<T> type)
	{
		return ed.getComponent(getMappedEntityId(entityId), type);
	}

	@Override
	public void addComponentChangeListener(ComponentChangeListener l)
	{
		this.componentChangeListeners.add(l);
	}

	@Override
	public void removeComponentChangeListener(ComponentChangeListener l)
	{
		this.componentChangeListeners.remove(l);
	}

}

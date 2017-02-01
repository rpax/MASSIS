package com.massisframework.massis.sim.ecs.zayes;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.TransformComponent;
import com.massisframework.massis.sim.ecs.CollectionsFactory;
import com.simsilica.es.ComponentFilter;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.base.ComponentHandler;
import com.simsilica.es.base.DefaultEntity;
import com.simsilica.es.base.DefaultEntityData;
import com.simsilica.es.base.DefaultEntitySet;

public class InterfaceEntityData extends DefaultEntityData
		implements SimulationEntityData {

	private InterfaceBindings bindings;
	private EntityComponentCreator componentCreator;
	private Map<Long, SimulationEntity> allEntities;
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
		this.allEntities = CollectionsFactory.newMap(Long.class,
				SimulationEntity.class);
	}

	@Override
	public SimulationEntity getSimulationEntity(EntityId id)
	{
		return this.getSimulationEntity(id.getId());
	}

	@Override
	public SimulationEntity getSimulationEntity(long id)
	{
		return (SimulationEntity) this.allEntities.get(id);
	}

	@Override
	public EntityId createEntity()
	{
		EntityId eid = super.createEntity();
		TransformComponent tc = this.addGet(eid, TransformComponent.class);
		ChildrenComponent cc = this.addGet(eid, ChildrenComponent.class);
		ParentComponent pc = this.addGet(eid, ParentComponent.class);
		this.allEntities.put(eid.getId(),
				new DefaultInterfaceEntity(this, eid,
						new SimulationComponent[] { tc, cc, pc },
						new Class[] {
								TransformComponent.class,
								ChildrenComponent.class,
								ParentComponent.class }));
		return eid;
	}

	@Override
	public void removeEntity(EntityId entityId)
	{
		// Note: because we only add the ComponentHandlers when
		// we encounter the component types... it's possible that
		// the entity stays orphaned with a few components if we
		// have never accessed any of them. SqlEntityData should
		// probably specifically be given types someday. FIXME

		// Remove all of its components
		for (Class c : handlers.keySet())
		{
			removeComponent(entityId, c);
		}
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
	public <T extends SimulationComponent> EntityEdit<T> add(EntityId entityId,
			Class<T> component)
	{
		//
		T cmp = addGet(entityId, component);
		return getEntityEdit(this.getSimulationEntity(entityId), cmp);
	}

	@Override
	public <T extends SimulationComponent> T addGet(
			EntityId entityId,
			Class<T> component)
	{
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

	private static ThreadLocal<ObjectPool<EntityEditImpl>> entityEditPool_TL = ThreadLocal
			.withInitial(() -> {
				return ObjectPool.create(EntityEditImpl.class,
						() -> new EntityEditImpl());
			});

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T extends SimulationComponent> EntityEdit<T> getEntityEdit(
			SimulationEntity se, T cmp)
	{
		ObjectPool<EntityEditImpl> objectPool = entityEditPool_TL.get();
		EntityEditImpl<T> entityEdit = objectPool.get();
		entityEdit.setObjectPool(objectPool);
		entityEdit.setCmp(cmp);
		entityEdit.setEd(this);
		entityEdit.setSe(se);
		return entityEdit;
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

}

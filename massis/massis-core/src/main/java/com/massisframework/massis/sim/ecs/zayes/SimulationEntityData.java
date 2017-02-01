package com.massisframework.massis.sim.ecs.zayes;

import java.util.Set;

import com.simsilica.es.ComponentFilter;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.StringIndex;
import com.simsilica.es.WatchedEntity;

public interface SimulationEntityData {

	public EntityId createEntity();

	public void removeEntity(EntityId entityId);

	public Entity getEntity(EntityId entityId, Class... types);

	public EntityId findEntity(ComponentFilter filter, Class... types);

	public Set<EntityId> findEntities(ComponentFilter filter, Class... types);

	public SimulationEntitySet createEntitySet(Class... types);

	public EntitySet getEntities(ComponentFilter filter, Class... types);

	public WatchedEntity watchEntity(EntityId entityId, Class... types);

	public StringIndex getStrings();

	public void close();

	/**
	 * @formatter:off
	 */
	public <T extends SimulationComponent> T add(EntityId entityId,Class<T> component);
	public <T extends SimulationComponent> void remove(EntityId entityId, Class<T> type);
	public <T extends SimulationComponent> T get(EntityId entityId,Class<T> type);
	/**
	 * @formatter:on
	 */

}

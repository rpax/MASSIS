package com.massisframework.massis.sim.ecs.zayes;

import com.simsilica.es.EntityId;

public interface SimulationEntityData {

	public EntityId createEntity();

	public void removeEntity(EntityId entityId);

	public SimulationEntitySet createEntitySet(Class... types);

	public SimulationEntity getSimulationEntity(EntityId entityId);

	public SimulationEntity getSimulationEntity(long id);

	public void close();

	/**
	 * @formatter:off
	 */
	public <T extends SimulationComponent> EntityEdit<T> add(EntityId entityId,Class<T> component);
	public <T extends SimulationComponent> void remove(EntityId entityId, Class<T> type);
	public <T extends SimulationComponent> T get(EntityId entityId,Class<T> type);
	public <T extends SimulationComponent> T addGet(EntityId entityId, Class<T> component);
	public <T extends SimulationComponent> Iterable<SimulationEntity> findEntities(Class...types);
	/**
	 * @formatter:on
	 */

}

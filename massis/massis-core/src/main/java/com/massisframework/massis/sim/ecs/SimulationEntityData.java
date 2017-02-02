package com.massisframework.massis.sim.ecs;

import com.simsilica.es.EntityId;

@SuppressWarnings("rawtypes")
public interface SimulationEntityData {

	public SimulationEntity createEntity();

	public void removeEntity(EntityId entityId);

	public SimulationEntitySet createEntitySet(
			Class... types);

	public SimulationEntity getSimulationEntity(EntityId entityId);

	public SimulationEntity getSimulationEntity(long id);

	public void close();

	/**
	 * @formatter:off
	 */
	public <T extends SimulationComponent> ComponentEdit<T> add(EntityId entityId,Class<T> component);
	public <T extends SimulationComponent> void remove(EntityId entityId, Class<T> type);
	public <T extends SimulationComponent> T get(EntityId entityId,Class<T> type);
	public <T extends SimulationComponent> Iterable<SimulationEntity> findEntities(Class...types);
	/**
	 * @formatter:on
	 */

	public void addComponentChangeListener(ComponentChangeListener l);

	public void removeComponentChangeListener(ComponentChangeListener l);

}

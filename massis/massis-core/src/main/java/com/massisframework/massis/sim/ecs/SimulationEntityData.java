package com.massisframework.massis.sim.ecs;

@SuppressWarnings("rawtypes")
public interface SimulationEntityData {

	public SimulationEntity createEntity();

	public void removeEntity(long entityId);

	public SimulationEntitySet createEntitySet(
			Class... types);

	public SimulationEntity getSimulationEntity(long id);

	public void close();

	/**
	 * @formatter:off
	 */
	public <T extends SimulationComponent> void add(long entityId, T cmp);
	public <T extends SimulationComponent> void remove(long entityId, Class<T> type);
	public <T extends SimulationComponent> T get(long entityId,Class<T> type);
	public <T extends SimulationComponent> Iterable<SimulationEntity> findEntities(Class...types);
	/**
	 * @formatter:on
	 */

	public void addComponentChangeListener(ComponentChangeListener l);

	public void removeComponentChangeListener(ComponentChangeListener l);

}

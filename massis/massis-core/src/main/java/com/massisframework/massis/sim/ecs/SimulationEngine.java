package com.massisframework.massis.sim.ecs;

import java.util.List;

public interface SimulationEngine<E extends OLDSimulationEntity<E>> {

	public long createEntity();

	public void destroyEntity(long eId);

	public OLDSimulationEntity<?> asSimulationEntity(long id);

	public void addSystem(Class<? extends SimulationSystem> system);

	public void removeSystem(Class<? extends SimulationSystem> system);

	public void start();

	public void stop();

	public List<OLDSimulationEntity<?>> getEntitiesFor(ComponentFilter<?> filter,
			List<OLDSimulationEntity<?>> store);

	public <T extends SimulationSystem> T getSystem(Class<T> type);
}

package com.massisframework.massis.sim.engine;

import com.massisframework.massis.model.components.SimulationComponent;
import com.massisframework.massis.sim.SimulationEntity;

@SuppressWarnings("rawtypes")
public interface SimulationEngine {

	public SimulationEntity createEntity();

	public void removeEntity(SimulationEntity entity);


	public Iterable<SimulationEntity> getEntitiesFor(Class... types);
	public <T extends SimulationSystem> void registerSystem(Class<T> system);
	public <T extends SimulationSystem> void registerSystems(Class... systems);
	public <T extends SimulationSystem> void unregisterSystem(Class<T> type);
	public <T extends SimulationSystem> T getSystem(Class<T> type);
	public <T extends SimulationComponent> T newComponent(Class<T> type);

	public void start();
	public void shutdown();

	public <T extends SimulationComponent> T getComponent(SimulationEntity entity,
			Class<T> type);

}

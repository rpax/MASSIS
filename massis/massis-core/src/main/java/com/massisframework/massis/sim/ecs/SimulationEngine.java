package com.massisframework.massis.sim.ecs;

import java.util.List;

public interface SimulationEngine {

	public int createEntity();

	public void destroyEntity(int eId);

	public SimulationEntity asSimulationEntity(int id);

	public void addSystem(Class<? extends SimulationSystem> system);

	public void removeSystem(Class<? extends SimulationSystem> system);

	public void start();

	public void stop();

	public List<SimulationEntity> getEntitiesFor(ComponentFilter filter,
			List<SimulationEntity> store);
}

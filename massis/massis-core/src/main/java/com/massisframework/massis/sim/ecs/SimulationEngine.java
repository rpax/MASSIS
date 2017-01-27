package com.massisframework.massis.sim.ecs;

public interface SimulationEngine {

	public int createEntity();

	public void destroyEntity(int eId);

	public SimulationEntity asSimulationEntity(int id);

	public void addSystem(Class<? extends SimulationSystem> system);

	public void removeSystem(Class<? extends SimulationSystem> system);

	public void start();

	public void stop();

	public void getEntitiesFor(ComponentFilter filter);
}

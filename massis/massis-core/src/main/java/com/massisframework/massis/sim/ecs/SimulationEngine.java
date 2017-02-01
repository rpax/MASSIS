package com.massisframework.massis.sim.ecs;

public interface SimulationEngine {

	public void addSystem(Class<? extends SimulationSystem> system);

	public void removeSystem(Class<? extends SimulationSystem> system);

	public void start();

	public void stop();

	public <T extends SimulationSystem> T getSystem(Class<T> type);
}

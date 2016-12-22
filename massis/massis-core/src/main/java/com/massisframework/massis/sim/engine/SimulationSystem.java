package com.massisframework.massis.sim.engine;

public interface SimulationSystem {

	
	public void addedToEngine(SimulationEngine simEngine);
	public void update(float deltaTime);
	public void removedFromEngine(SimulationEngine simEngine);
}

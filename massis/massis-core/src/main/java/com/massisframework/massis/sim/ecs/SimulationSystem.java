package com.massisframework.massis.sim.ecs;

public interface SimulationSystem {

	public void addedToEngine(SimulationEngine engine);

	public void initialize();	

	public void update(float deltaTime);
	
	public void removedFromEngine(SimulationEngine engine);

	


}

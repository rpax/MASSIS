package com.massisframework.massis.sim;

public interface SimulationSteppable {

	public void step(SimulationScheduler scheduler, float deltaTime);

}

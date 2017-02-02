package com.massisframework.massis.sim.ecs;

public interface ComponentChangeListener {

	public void componentInserted(SimulationComponent cmp);
	public void componentRemoved(SimulationComponent cmp);
	
}

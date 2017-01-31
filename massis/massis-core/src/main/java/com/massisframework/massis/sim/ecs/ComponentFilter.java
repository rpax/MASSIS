package com.massisframework.massis.sim.ecs;

public interface ComponentFilter<E extends SimulationEntity<E>> {

	public boolean matches(E entity);
	
	
}

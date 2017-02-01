package com.massisframework.massis.sim.ecs;

public interface ComponentFilter<E extends OLDSimulationEntity<E>> {

	public boolean matches(E entity);
	
	
}

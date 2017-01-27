package com.massisframework.massis.sim.ecs;

import java.util.Set;

public interface ComponentConfiguration {

	public Set<Class<? extends SimulationComponent>> getRegisteredComponents();

	public Class<? extends SimulationComponent> getMapping(
			Class<? extends SimulationComponent> itf);

}

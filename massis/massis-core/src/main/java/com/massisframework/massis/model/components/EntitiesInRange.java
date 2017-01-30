package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;

public interface EntitiesInRange extends SimulationComponent {

	Iterable<SimulationEntity> get();

}

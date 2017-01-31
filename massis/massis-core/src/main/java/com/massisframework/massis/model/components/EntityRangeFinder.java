package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;

public interface EntityRangeFinder extends SimulationComponent {

	Iterable<SimulationEntity<?>> getEntitiesInRange(double radius);

}

package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.OLDSimulationEntity;

public interface EntityRangeFinder extends SimulationComponent {

	Iterable<OLDSimulationEntity<?>> getEntitiesInRange(double radius);

}

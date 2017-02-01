package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.OLDSimulationEntity;
import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;

public interface EntityRangeFinder extends SimulationComponent {

	Iterable<OLDSimulationEntity<?>> getEntitiesInRange(double radius);

}

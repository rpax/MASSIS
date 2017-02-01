package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntity;

public interface EntityRangeFinder extends SimulationComponent {

	public Iterable<SimulationEntity> getEntitiesInRange(double radius);

}

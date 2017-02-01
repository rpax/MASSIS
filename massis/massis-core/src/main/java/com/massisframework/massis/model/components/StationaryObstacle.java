package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;

import straightedge.geom.path.PathBlockingObstacle;

public interface StationaryObstacle extends SimulationComponent {

	public PathBlockingObstacle getObstacle();

}

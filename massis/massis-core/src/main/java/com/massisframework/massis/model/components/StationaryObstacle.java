package com.massisframework.massis.model.components;

import java.util.List;

import com.massisframework.massis.sim.ecs.SimulationComponent;

import straightedge.geom.path.PathBlockingObstacle;

public interface StationaryObstacle extends SimulationComponent {

	public List<PathBlockingObstacle> getObstacles();

}

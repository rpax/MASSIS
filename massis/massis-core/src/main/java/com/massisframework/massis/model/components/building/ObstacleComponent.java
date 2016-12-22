package com.massisframework.massis.model.components.building;

import com.massisframework.massis.model.components.SimulationComponent;

import straightedge.geom.path.PathBlockingObstacle;

public interface ObstacleComponent extends SimulationComponent{

	public PathBlockingObstacle getObstacle();
	public void setObstacle(PathBlockingObstacle obstacle);
}

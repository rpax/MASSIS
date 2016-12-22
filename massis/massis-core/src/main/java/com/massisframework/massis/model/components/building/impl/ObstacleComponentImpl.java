package com.massisframework.massis.model.components.building.impl;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.building.ObstacleComponent;

import straightedge.geom.path.PathBlockingObstacle;

public class ObstacleComponentImpl implements ObstacleComponent {

	private PathBlockingObstacle obstacle;
	@Inject
	private ObstacleComponentImpl()
	{
		
	}

	@Override
	public PathBlockingObstacle getObstacle()
	{
		return this.obstacle;
	}

	public void setObstacle(PathBlockingObstacle obstacle)
	{
		this.obstacle = obstacle;
	}

}

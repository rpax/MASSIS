package com.massisframework.massis.model.components.impl;

import com.massisframework.massis.model.components.StationaryObstacle;

import straightedge.geom.path.PathBlockingObstacle;

public class StationaryObstacleImpl implements StationaryObstacle {

	private PathBlockingObstacle obstacle;
	
	public void setObstacle(PathBlockingObstacle obstacle){
		this.obstacle=obstacle;
	}
	@Override
	public PathBlockingObstacle getObstacle()
	{
		return this.obstacle;
	}

}

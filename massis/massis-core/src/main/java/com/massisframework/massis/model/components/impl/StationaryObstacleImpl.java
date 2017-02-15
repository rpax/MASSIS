package com.massisframework.massis.model.components.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.massisframework.massis.model.components.StationaryObstacle;

import straightedge.geom.path.PathBlockingObstacle;
import straightedge.geom.path.PathBlockingObstacleImpl;

public class StationaryObstacleImpl implements StationaryObstacle {

	private List<PathBlockingObstacle> obstacles = new ArrayList<>();

	@Override
	public List<PathBlockingObstacle> getObstacles()
	{
		return Collections.unmodifiableList(this.obstacles);
	}

	public void addObstacle(PathBlockingObstacleImpl obst)
	{
		this.obstacles.add(obst);
	}

	public void clearObstacles()
	{
		this.obstacles.clear();
	}

}

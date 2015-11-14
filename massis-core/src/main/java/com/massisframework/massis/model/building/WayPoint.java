package com.massisframework.massis.model.building;

import com.massisframework.massis.model.managers.pathfinding.PathFollower;
import com.massisframework.massis.util.geom.CoordinateHolder;

public interface WayPoint extends CoordinateHolder{
	public boolean canExecuteWayPointAction(PathFollower pf);
	public boolean executeWayPointAction(PathFollower pf);
}

package com.massisframework.massis.model.managers.pathfinding;

import com.massisframework.massis.model.building.LocationHolder;
import com.massisframework.massis.model.building.Movable;
import com.massisframework.massis.model.managers.movement.Path;
import com.massisframework.massis.model.managers.movement.steering.SteeringCapable;

import straightedge.geom.PolygonHolder;

/**
 * Interface representing an element following or containing a {@link Path}.
 * 
 * @author rpax
 *
 */
public interface PathFollower extends LocationHolder, PolygonHolder,SteeringCapable,Movable {

	/**
	 * 
	 * @return if this element contains a valid path
	 */
	public boolean hasPath();

	/**
	 * 
	 * @return the path that is being followed by this agent. It is <b>Highly
	 *         recommended</b> to call first {@link #hasPath()} before. If
	 *         {@link #hasPath()} returns <code>false</code>, the result is
	 *         undefined. (Can be null, an older path...etc).
	 */
	public Path getPath();
}

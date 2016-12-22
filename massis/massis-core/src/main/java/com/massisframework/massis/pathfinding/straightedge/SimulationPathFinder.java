package com.massisframework.massis.pathfinding.straightedge;

import java.util.List;

import com.massisframework.massis.model.components.Location;

import straightedge.geom.KPoint;

public interface SimulationPathFinder {

	List<KPoint> findPath(Location from, Location toLoc);

}

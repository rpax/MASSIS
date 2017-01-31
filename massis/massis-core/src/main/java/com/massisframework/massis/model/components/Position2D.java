package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.util.geom.CoordinateHolder;

import straightedge.geom.KPoint;

public interface Position2D extends SimulationComponent, CoordinateHolder {

	public void set(double x, double y);

	public KPoint getXY();

	public KPoint getWorldPosition();

	public default double distance(Position2D other)
	{
		return this.getXY().distance(other.getXY());
	}
}

package com.massisframework.massis.model.components;

import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.util.geom.CoordinateHolder;

public interface Location extends SimulationComponent,CoordinateHolder{

	boolean isInSameFloor(Location other);

	double distance2D(Location other);

	double distance2D(double x, double y);

	boolean isInFloor(Floor f);

	Floor getFloor();

}
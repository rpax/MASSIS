package com.massisframework.massis.model.components.building;

import com.massisframework.massis.model.components.SimulationComponent;
import com.massisframework.massis.util.geom.CoordinateHolder;

public interface Coordinate2DComponent
		extends CoordinateHolder, SimulationComponent {

	public void setX(double x);

	public void setY(double y);
}

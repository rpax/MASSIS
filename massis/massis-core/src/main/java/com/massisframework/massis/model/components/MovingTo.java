package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.util.geom.CoordinateHolder;

public interface MovingTo extends SimulationComponent {

	CoordinateHolder getTarget();

	void setTarget(CoordinateHolder target);

}
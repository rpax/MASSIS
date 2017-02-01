package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;
import com.massisframework.massis.util.geom.CoordinateHolder;

public interface FollowTarget extends SimulationComponent {

	CoordinateHolder getTarget();

	void setTarget(CoordinateHolder target);

}
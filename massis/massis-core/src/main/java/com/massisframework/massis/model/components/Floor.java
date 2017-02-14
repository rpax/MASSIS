package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.SimulationComponent;

public interface Floor extends SimulationComponent{

	int getMinX();

	int getMaxX();

	int getMinY();

	int getMaxY();

}
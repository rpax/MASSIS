package com.massisframework.massis.model.systems.floor;

import com.massisframework.massis.sim.ecs.SimulationComponent;

public interface Floor extends SimulationComponent{

	int getMinX();

	int getMaxX();

	int getMinY();

	int getMaxY();

}
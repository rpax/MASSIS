package com.massisframework.massis.model.components.building;

import com.massisframework.massis.model.components.SimulationComponent;

public interface MovementCapabilities extends SimulationComponent{

	boolean isObstacle();

	boolean canMove();

}
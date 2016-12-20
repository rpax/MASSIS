package com.massisframework.massis.model.components.building;

import com.massisframework.massis.model.components.SimulationComponent;

public interface HeadingComponent extends SimulationComponent{

	public double getAngle();
	
	public double getHeadingX();
	
	public double getHeadingY();
}
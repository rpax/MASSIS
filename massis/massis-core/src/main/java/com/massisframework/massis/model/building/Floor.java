package com.massisframework.massis.model.building;

import com.massisframework.massis.model.components.SimulationComponent;

public interface Floor extends SimulationComponent{


	int getMinX();

	int getMaxX();

	int getMinY();

	int getMaxY();

	public int getXlength();

	public int getYlength();


	String getName();


}
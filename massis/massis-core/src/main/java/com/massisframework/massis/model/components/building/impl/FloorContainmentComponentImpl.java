package com.massisframework.massis.model.components.building.impl;

import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.components.building.FloorContainmentComponent;

public class FloorContainmentComponentImpl
		extends AbstractSimulationComponent
		implements FloorContainmentComponent {

	private Floor floor;

	public FloorContainmentComponentImpl(Floor floor)
	{
		this.floor = floor;
	}

	@Override
	public Floor getFloor()
	{
		return this.floor;
	}

	@Override
	public void setFloor(Floor f)
	{
		this.floor = f;
		this.fireChanged();
	}

}

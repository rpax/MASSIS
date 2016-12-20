package com.massisframework.massis.model.components.building.impl;

import com.massisframework.massis.model.components.building.FloorContainmentComponent;

public class FloorContainmentComponentImpl 
		implements FloorContainmentComponent {

	
	private long floorId;

	@Override
	public long getFloorId()
	{
		return this.floorId;
	}

	public void setFloorId(long floorId)
	{
		this.floorId = floorId;
	}

}

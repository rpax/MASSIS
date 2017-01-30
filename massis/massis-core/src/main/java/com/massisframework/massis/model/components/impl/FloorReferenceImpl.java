package com.massisframework.massis.model.components.impl;

import com.massisframework.massis.model.components.FloorReference;

public class FloorReferenceImpl implements FloorReference {

	private int floorId;

	@Override
	public void setFloorId(int fId)
	{
		this.floorId = fId;

	}

	@Override
	public int getFloorId()
	{
		return this.floorId;
	}

}

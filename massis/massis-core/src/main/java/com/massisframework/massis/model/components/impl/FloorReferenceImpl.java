package com.massisframework.massis.model.components.impl;

import com.massisframework.massis.model.components.FloorReference;

public class FloorReferenceImpl implements FloorReference {

	private long floorId;

	@Override
	public void setFloorId(long fId)
	{
		this.floorId = fId;

	}

	@Override
	public long getFloorId()
	{
		return this.floorId;
	}

}

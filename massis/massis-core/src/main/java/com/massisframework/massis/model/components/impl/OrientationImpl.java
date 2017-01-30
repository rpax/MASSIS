package com.massisframework.massis.model.components.impl;

import com.massisframework.massis.model.components.Orientation;

public class OrientationImpl implements Orientation {

	private double angle;

	@Override
	public double getAngle()
	{
		return this.angle;
	}

	@Override
	public Orientation setAngle(float angle)
	{
		// TODO rotate polygon here?
		this.angle = angle;
		return this;
	}

}

package com.massisframework.massis.model.components.building.impl;

import com.massisframework.massis.model.components.building.HeadingComponent;

import straightedge.geom.KPoint;

public class HeadingImpl extends AbstractSimulationComponent
		implements HeadingComponent {


	private double headingX;

	private double headingY;

	public HeadingImpl(){
		this.headingX=0;
		this.headingY=0;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.components.building.impl.
	 * RotationComponent#getAngle()
	 */
	@Override
	public double getAngle()
	{
		return KPoint.findAngle(0, 0, headingX, headingY);
	}

	@Override
	public double getHeadingX()
	{
		return this.headingX;
	}

	@Override
	public double getHeadingY()
	{
		return this.headingY;
	}

}

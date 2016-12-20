package com.massisframework.massis.model.components.building.impl;

import com.massisframework.massis.model.components.building.Coordinate2DComponent;

public class Coordinate2DComponentImpl extends AbstractSimulationComponent implements Coordinate2DComponent{

	private double x;
	
	private double y;

	@Override
	public double getX()
	{
		return this.x;
	}

	@Override
	public double getY()
	{
		return this.y;
	}

	@Override
	public void setX(double x)
	{
		this.x=x;
		this.fireChanged();
	}

	@Override
	public void setY(double y)
	{
		this.y=y;
		this.fireChanged();
	}

}

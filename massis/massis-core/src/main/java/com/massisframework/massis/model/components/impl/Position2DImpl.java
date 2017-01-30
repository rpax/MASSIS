package com.massisframework.massis.model.components.impl;

import com.massisframework.massis.model.components.Position2D;

import straightedge.geom.KPoint;

public class Position2DImpl implements Position2D {

	private KPoint pos = new KPoint();

	@Override
	public double getX()
	{
		return pos.x;
	}

	@Override
	public double getY()
	{
		return pos.y;
	}

	@Override
	public void set(double x, double y)
	{
		this.pos.setCoords(x, y);

	}

	@Override
	public KPoint getXY()
	{
		return this.pos;
	}

}

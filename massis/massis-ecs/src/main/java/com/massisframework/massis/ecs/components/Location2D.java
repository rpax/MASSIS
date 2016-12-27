package com.massisframework.massis.ecs.components;

import straightedge.geom.KPoint;

public class Location2D extends ModifiableComponent {

	private KPoint point = new KPoint();
	private float y = 0;

	public Location2D()
	{

	}

	public void set(KPoint point)
	{
		this.setX(point.x);
		this.setZ(point.y);
		this.setY(0);
	}

	public Location2D setX(double x)
	{
		this.point.x = x;
		this.fireChanged();
		return this;
	}

	private void setY(double y)
	{
		this.y = (float) y;
		this.fireChanged();
	}

	public Location2D setZ(double z)
	{
		this.point.y = z;
		this.fireChanged();
		return this;
	}

	public float getX()
	{
		return (float) point.x;
	}

	public float getZ()
	{
		return (float) point.y;
	}

}

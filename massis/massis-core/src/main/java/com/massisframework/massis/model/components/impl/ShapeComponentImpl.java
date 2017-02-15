package com.massisframework.massis.model.components.impl;

import java.awt.Shape;

import com.massisframework.massis.model.components.ShapeComponent;

import straightedge.geom.KPolygon;

public class ShapeComponentImpl implements ShapeComponent {

	private KPolygon polygon;

	// @Override
	public KPolygon getShape()
	{
		return this.polygon;
	}

	//
	public void setShape(KPolygon shape)
	{
		this.polygon = shape;
	}

	@Override
	public double getRadius()
	{
		return this.polygon.getRadius();
	}

	@Override
	public int getNumPoints()
	{
		return this.polygon.points.size();
	}

	@Override
	public double centerX()
	{
		return this.polygon.getCenter().x;
	}

	@Override
	public double centerY()
	{
		return this.polygon.getCenter().y;
	}

	@Override
	public double getX(int i)
	{
		return this.polygon.getPoint(i).x;
	}

	@Override
	public double getY(int i)
	{
		return this.polygon.getPoint(i).y;
	}

	@Override
	public void translateTo(float x, float y)
	{
		this.polygon.translateTo(x, y);

	}

	@Override
	public void translateTo(double x, double y)
	{
		this.translateTo((float) x, (float) y);
	}

	@Override
	public Shape asShape()
	{
		return this.polygon;
	}

}

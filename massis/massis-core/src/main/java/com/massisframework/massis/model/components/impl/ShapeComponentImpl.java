package com.massisframework.massis.model.components.impl;

import com.massisframework.massis.model.components.ShapeComponent;

import straightedge.geom.KPolygon;

public class ShapeComponentImpl implements ShapeComponent {

	private KPolygon polygon;

	@Override
	public KPolygon getShape()
	{
		return this.polygon;
	}

	public void setShape(KPolygon shape)
	{
		this.polygon = shape;
	}

	@Override
	public double getRadius()
	{
		return this.polygon.getRadius();
	}

}

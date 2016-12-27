package com.massisframework.massis.ecs.components;

import straightedge.geom.KPolygon;

public class PolygonComponent extends ModifiableComponent {

	private KPolygon polygon;

	public PolygonComponent()
	{

	}

	public PolygonComponent(KPolygon kPolygon)
	{
		this.polygon = kPolygon;
	}

	public KPolygon get()
	{
		return polygon;
	}

	public void set(KPolygon polygon)
	{
		this.polygon = polygon;
	}
}

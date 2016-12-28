package com.massisframework.massis.ecs.components;

import com.artemis.Component;

import straightedge.geom.KPolygon;

public class PolygonComponent extends Component {

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

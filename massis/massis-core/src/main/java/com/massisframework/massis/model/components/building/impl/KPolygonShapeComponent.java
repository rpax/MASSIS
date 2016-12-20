package com.massisframework.massis.model.components.building.impl;

import java.awt.Shape;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.massisframework.massis.model.components.building.Coordinate2DComponent;
import com.massisframework.massis.model.components.building.HeadingComponent;
import com.massisframework.massis.model.components.building.ShapeComponent;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;
import straightedge.geom.PolygonHolder;

public class KPolygonShapeComponent extends AbstractSimulationComponent
		implements
		ShapeComponent,
		PolygonHolder {

	private KPolygon polygon;
	private double oldAngle = 0;

	public KPolygonShapeComponent(float[][] points)
	{
		this(Arrays.stream(points)
				.map(p -> new KPoint(p[0], p[1]))
				.collect(Collectors.toList()));
	}

	public KPolygonShapeComponent(List<KPoint> points)
	{
		this(points.toArray(new KPoint[] {}));
	}

	public KPolygonShapeComponent(KPoint[] points)
	{
		this.polygon = new KPolygon(points);
	}

	public KPolygonShapeComponent(KPolygon polygon)
	{
		this.polygon = new KPolygon(polygon);
	}

	@Override
	public KPolygon getPolygon()
	{
		return this.polygon;
	}

	@Override
	public Shape getShape()
	{
		return this.getPolygon();
	}

	@Override
	public void step(float tpf)
	{
		// Update the shape accordingly to the location
		Coordinate2DComponent coord = this.getEntity()
				.get(Coordinate2DComponent.class);
		HeadingComponent rot = this.getEntity().get(HeadingComponent.class);
		final double oldX = this.polygon.getCenter().x;
		final double oldY = this.polygon.getCenter().y;
		final double newAngle = rot.getAngle();
		boolean changed = false;
		if (oldX != coord.getX() || oldY != coord.getY())
		{
			this.polygon.translate(coord.getX(), coord.getY());
			changed = true;
		}
		if (this.oldAngle != newAngle)
		{
			this.polygon.rotate(newAngle - oldAngle);
			this.oldAngle = newAngle;
			changed = true;
		}
		if (changed)
		{
			this.fireChanged();
		}

	}

}

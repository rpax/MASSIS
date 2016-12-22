package com.massisframework.massis.model.components.building.impl;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.massisframework.massis.model.components.Location;
import com.massisframework.massis.model.components.building.HeadingComponent;
import com.massisframework.massis.model.components.building.ShapeComponent;
import com.massisframework.massis.util.geom.KPolygonUtils;

import straightedge.geom.AABB;
import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;
import straightedge.geom.PolygonHolder;

public class KPolygonShapeComponent extends AbstractSimulationComponent
		implements
		ShapeComponent,
		PolygonHolder {

	private static final Shape EMPTY_SHAPE=new Area();
	private KPolygon polygon;
	private AABB aabb;
	private double oldAngle = 0;
	private Rectangle2D bounds;
	
	private KPolygonShapeComponent()
	{
		
	}
	@Override
	public void setShape(float[][] points)
	{
		setShape(Arrays.stream(points)
				.map(p -> new KPoint(p[0], p[1]))
				.collect(Collectors.toList()));
	}
	@Override
	public void setShape(Shape s)
	{
		setShape(KPolygonUtils.createKPolygonFromShape(s, false));
	}
	private void setShape(List<KPoint> points)
	{
		setShape(points.toArray(new KPoint[] {}));
	}

	private void setShape(KPoint[] points)
	{
		this.polygon = new KPolygon(points);
	}

	private void setShape(KPolygon polygon)
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
		if (this.polygon==null){
			return EMPTY_SHAPE;
		}
		return this.getPolygon();
	}
	@Deprecated
	@Override
	public void step(float tpf)
	{
		// Update the shape accordingly to the location
		Location loc = this.getEntity().get(Location.class);
		double angleOffset = 0;
		HeadingComponent rot = this.getEntity().get(HeadingComponent.class);
		if (rot != null)
		{
			angleOffset = oldAngle - rot.getAngle();
		}

		final double oldX = this.polygon.getCenter().x;
		final double oldY = this.polygon.getCenter().y;

		boolean changed = false;
		if (oldX != loc.getX() || oldY != loc.getY())
		{
			this.polygon.translate(loc.getX(), loc.getY());
			changed = true;
		}
		if (angleOffset > 0.0001)
		{
			this.polygon.rotate(angleOffset);
			this.oldAngle += angleOffset;
			changed = true;
		}
		if (changed)
		{
			this.invalidate();
		}

	}

	@Override
	public boolean intersects(Shape s)
	{
		if (s instanceof KPolygon)
		{
			KPolygon other = (KPolygon) s;
			if (this.polygon.intersectionPossible(other))
			{
				return this.polygon.intersects(other);
			} else
			{
				return false;
			}
		} else
		{
			return intersects(KPolygonUtils.createKPolygonFromShape(s, true));
		}

	}

	@Override
	public boolean intersects(ShapeComponent s)
	{
		return this.intersects(s.getShape());
	}

	@Override
	public boolean intersectsAABB(Shape other)
	{
		final double x = this.getAABB().x();
		final double y = this.getAABB().y();
		final double w = this.getAABB().w();
		final double h = this.getAABB().h();
		return other.intersects(x, y, w, h);
	}

	private AABB getAABB()
	{
		if (this.aabb == null)
		{
			this.aabb = this.polygon.getAABB();
		}
		return this.aabb;
	}

	@Override
	public boolean intersectsAABB(ShapeComponent other)
	{
		return this.intersectsAABB(other.getShape());
	}

	@Override
	public Rectangle2D getBounds()
	{
		if (this.bounds == null)
		{
			this.bounds = this.polygon.getBounds2D();
		}
		return this.bounds;
	}

	private void invalidate()
	{
		this.bounds = null;
		this.aabb = null;
	}

	@Override
	public double getRadius()
	{
		return this.polygon.getRadius();
	}

	

}

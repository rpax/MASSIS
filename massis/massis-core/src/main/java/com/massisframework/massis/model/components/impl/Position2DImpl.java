package com.massisframework.massis.model.components.impl;

import com.massisframework.massis.model.components.Position2D;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.injection.components.EntityReference;

import straightedge.geom.KPoint;

public class Position2DImpl implements Position2D {

	private KPoint pos = new KPoint();
	private KPoint worldPosition = new KPoint();

	@EntityReference
	SimulationEntity<?> entity;

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

	public KPoint getWorldPosition()
	{
		// transform...
		this.worldPosition.setCoords(this.pos);
		SimulationEntity<?> e = entity.getParent();
		while (e != null)
		{
			KPoint parentPos = e.get(Position2D.class).getXY();
			this.worldPosition.x += parentPos.x;
			this.worldPosition.y += parentPos.y;
			e = e.getParent();
		}
		return this.worldPosition;
	}
}

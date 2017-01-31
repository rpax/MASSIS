package com.massisframework.massis.model;

import com.massisframework.gui.DrawableZone;
import com.massisframework.massis.model.components.Floor;
import com.massisframework.massis.sim.ecs.SimulationEntity;

public class DrawableFloor implements DrawableZone {

	private SimulationEntity floor;

	public DrawableFloor(SimulationEntity floor)
	{
		this.floor = floor;
	}

	@Override
	public float getMaxX()
	{
		return this.floor.get(Floor.class).getMaxX();
	}

	@Override
	public float getMaxY()
	{
		return this.floor.get(Floor.class).getMaxY();
	}

	@Override
	public float getMinX()
	{
		return this.floor.get(Floor.class).getMinX();
	}

	@Override
	public float getMinY()
	{
		return this.floor.get(Floor.class).getMinY();
	}

	@Override
	public String getName()
	{
		return this.floor.get(NameComponent.class).get();
	}

	public SimulationEntity getFloor()
	{
		return this.floor;
	}

}

package com.massisframework.massis.displays.floormap.layers;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.massisframework.gui.DrawableZone;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.sim.SimulationEntity;
import com.massisframework.massis.sim.engine.SimulationEngine;

public class DrawableFloor implements DrawableZone {

	private Floor floor;
	private SimulationEngine engine;

	public DrawableFloor(Floor f, SimulationEngine engine)
	{
		this.floor = f;
		this.engine = engine;
	}

	@Override
	public float getMaxX()
	{
		return this.floor.getMaxX();
	}

	@Override
	public float getMaxY()
	{
		return this.floor.getMaxY();
	}

	@Override
	public float getMinX()
	{
		return this.floor.getMinX();
	}

	@Override
	public float getMinY()
	{
		return this.floor.getMinY();
	}

	@Override
	public String getName()
	{
		return this.floor.getName();
	}

	protected Iterable<SimulationEntity> getEntitiesFor(Class... types)
	{
		return this.engine.getEntitiesFor(types);

	}

	protected Stream<SimulationEntity> getEntitiesForStream(Class... types)
	{
		return StreamSupport
				.stream(this.getEntitiesFor(types).spliterator(), false);

	}

	public int getYlength()
	{
		return this.floor.getYlength();
	}

	public int getXlength()
	{
		return this.floor.getXlength();
	}

}

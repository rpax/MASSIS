package com.massisframework.gui;

import com.google.inject.Provider;
import com.massisframework.massis.model.components.Floor;
import com.massisframework.massis.model.components.NameComponent;
import com.massisframework.massis.sim.ecs.ComponentFilterBuilder;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationEntity;

public class EngineDrawableZone implements DrawableZone {

	private int floorId;
	private Provider<ComponentFilterBuilder> cFBuilderProv;
	private SimulationEngine engine;

	public EngineDrawableZone(int floorId,
			Provider<ComponentFilterBuilder> cFBuilderProv,
			SimulationEngine engine)
	{
		this.floorId = floorId;
		this.cFBuilderProv = cFBuilderProv;
		this.engine = engine;
	}

	@Override
	public float getMaxX()
	{
		return this.engine.asSimulationEntity(floorId).get(Floor.class)
				.getMaxX();
	}

	@Override
	public float getMaxY()
	{
		return this.engine.asSimulationEntity(floorId).get(Floor.class)
				.getMaxY();
	}

	@Override
	public float getMinX()
	{
		return this.engine.asSimulationEntity(floorId).get(Floor.class)
				.getMinX();
	}

	@Override
	public float getMinY()
	{
		return this.engine.asSimulationEntity(floorId).get(Floor.class)
				.getMinY();
	}

	@Override
	public String getName()
	{
		return this.engine.asSimulationEntity(floorId).get(NameComponent.class)
				.get();
	}

	public SimulationEntity getFloor()
	{
		return this.engine.asSimulationEntity(floorId);
	}
}

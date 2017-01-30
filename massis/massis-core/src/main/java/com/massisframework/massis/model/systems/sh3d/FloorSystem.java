package com.massisframework.massis.model.systems.sh3d;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.ComponentFilterBuilder;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.injection.SimulationConfiguration;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public class FloorSystem implements SimulationSystem {

	@Inject
	SimulationEngine engine;
	@Inject
	SimulationConfiguration configuration;

	@Inject
	Provider<ComponentFilterBuilder> filterBuilder;

	private Int2ObjectMap<SimulationEntity> floors;

	private List<SimulationEntity> cacheList = new ArrayList<>();

	private ComponentFilter levelFilter;
	private ComponentFilter wallFilter;
	private ComponentFilter roomFilter;
	private ComponentFilter furnitureFilter;

	@Override
	public void initialize()
	{
		this.levelFilter = filterBuilder.get()
				.all(SweetHome3DLevel.class)
				.get();
		this.wallFilter = filterBuilder.get()
				.all(SweetHome3DWall.class).get();
		this.roomFilter = filterBuilder.get()
				.all(SweetHome3DRoom.class).get();
		this.furnitureFilter = filterBuilder.get()
				.all(SweetHome3DFurniture.class).get();
	}

	@Override
	public void update(float deltaTime)
	{
		
	}

}

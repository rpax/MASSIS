package com.massisframework.massis.model.systems.sh3d;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.massisframework.massis.sim.ecs.ComponentFilterBuilder;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.injection.SimulationConfiguration;

public class FloorSystem implements SimulationSystem {

	@Inject
	SimulationEngine engine;
	@Inject
	SimulationConfiguration configuration;

	@Inject
	Provider<ComponentFilterBuilder> filterBuilder;


	@Override
	public void initialize()
	{
		
	}

	@Override
	public void update(float deltaTime)
	{
		
	}

}

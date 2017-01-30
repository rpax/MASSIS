package com.massisframework.massis.model.systems.sh3d;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.Floor;
import com.massisframework.massis.model.components.FloorReference;
import com.massisframework.massis.model.components.impl.FloorImpl;
import com.massisframework.massis.sim.FilterParams;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.injection.SimulationConfiguration;

public class FloorSystem implements SimulationSystem {

	@Inject
	SimulationEngine engine;
	@Inject
	SimulationConfiguration configuration;

	

	

	@Override
	public void initialize()
	{
		
	}

	@Override
	public void update(float deltaTime)
	{
		

	}

}

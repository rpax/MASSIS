package com.massisframework.massis.model.systems.sh3d;

import com.google.inject.Inject;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationSystem;

public class Visualization3DSystem implements SimulationSystem {

	@Inject
	private SimulationEngine engine;

	@Override
	public void initialize()
	{
		//
	}

	@Override
	public void update(float deltaTime)
	{
		// TODO Auto-generated method stub

	}

}

package com.massisframework.massis.sim.ecs.ashley;

import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;

public class AshleyEntityIdReference implements SimulationComponent {

	public int ashleyId;

	public AshleyEntityIdReference(int ashleyId)
	{
		this.ashleyId = ashleyId;
	}
}

package com.massisframework.massis.sim.ecs.ashley;

import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;

public class AshleySimulationEntityReference implements SimulationComponent {

	private SimulationEntity reference;

	public AshleySimulationEntityReference(SimulationEntity reference)
	{
		this.reference = reference;
	}

	public SimulationEntity getReference()
	{
		return reference;
	}

}

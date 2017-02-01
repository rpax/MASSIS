package com.massisframework.massis.sim.ecs.ashley;

import com.massisframework.massis.sim.ecs.OLDSimulationEntity;
import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;

public class AshleySimulationEntityReference implements SimulationComponent {

	private OLDSimulationEntity reference;

	public AshleySimulationEntityReference(OLDSimulationEntity reference)
	{
		this.reference = reference;
	}

	public OLDSimulationEntity getReference()
	{
		return reference;
	}

}

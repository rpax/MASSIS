package com.massisframework.massis.sim.ecs.ashley;

import com.badlogic.ashley.core.Family;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.SimulationEntity;

public class AshleyComponentFilter implements ComponentFilter {

	private Family family;

	public AshleyComponentFilter(Family family)
	{
		this.family = family;
	}

	@Override
	public boolean matches(SimulationEntity entity)
	{
		return this.family
				.matches(((AshleySimulationEntity) entity).getEntity());
	}

	public Family getFamily()
	{
		return family;
	}

}

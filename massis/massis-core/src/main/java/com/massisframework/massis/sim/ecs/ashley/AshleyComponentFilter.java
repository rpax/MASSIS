package com.massisframework.massis.sim.ecs.ashley;

import com.badlogic.ashley.core.Family;
import com.massisframework.massis.sim.ecs.ComponentFilter;

public class AshleyComponentFilter implements ComponentFilter<AshleySimulationEntity> {

	private Family family;

	public AshleyComponentFilter(Family family)
	{
		this.family = family;
	}

	@Override
	public boolean matches(AshleySimulationEntity entity)
	{
		return this.family
				.matches(entity.getEntity());
	}

	public Family getFamily()
	{
		return family;
	}

}

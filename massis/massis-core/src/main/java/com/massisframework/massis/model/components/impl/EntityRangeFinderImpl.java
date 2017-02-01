package com.massisframework.massis.model.components.impl;

import com.massisframework.massis.model.components.EntityRangeFinder;
import com.massisframework.massis.model.managers.EnvironmentManager;
import com.massisframework.massis.sim.ecs.injection.components.EntityReference;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntity;

public class EntityRangeFinderImpl implements EntityRangeFinder {

	@EntityReference
	private SimulationEntity entity;

	@Override
	public Iterable<SimulationEntity> getEntitiesInRange(double radius)
	{
		throw new UnsupportedOperationException();
	}

}

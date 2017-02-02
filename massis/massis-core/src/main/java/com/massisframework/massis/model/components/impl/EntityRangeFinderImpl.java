package com.massisframework.massis.model.components.impl;

import com.massisframework.massis.model.components.EntityRangeFinder;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.injection.components.EntityReference;

public class EntityRangeFinderImpl implements EntityRangeFinder {

	@EntityReference
	private SimulationEntity entity;

	@Override
	public Iterable<SimulationEntity> getEntitiesInRange(double radius)
	{
		throw new UnsupportedOperationException();
	}

}

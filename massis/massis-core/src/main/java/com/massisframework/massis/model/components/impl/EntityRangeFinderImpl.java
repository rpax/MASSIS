package com.massisframework.massis.model.components.impl;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.EntityRangeFinder;
import com.massisframework.massis.model.managers.EnvironmentManager;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.injection.components.EntityReference;

public class EntityRangeFinderImpl implements EntityRangeFinder {

	@EntityReference
	private SimulationEntity entity;

	@Inject
	private SimulationEngine engine;

	@Override
	public Iterable<SimulationEntity> getEntitiesInRange(double radius)
	{
		return this.engine.getSystem(EnvironmentManager.class)
				.getAgentsInRange(this.entity, radius);
	}

}

package com.massisframework.massis.model.managers;

import java.util.stream.StreamSupport;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.Floor;
import com.massisframework.massis.model.components.FloorReference;
import com.massisframework.massis.model.components.Position2D;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationSystem;

/**
 * Manages the environment information of an agent
 *
 * @author rpax
 *
 */
public class EnvironmentManager implements SimulationSystem {

	@Inject
	private SimulationEngine engine;

	@Override
	public void initialize()
	{

	}

	public Iterable<SimulationEntity> getAgentsInRange(SimulationEntity entity,
			double radius)
	{
		int floorId = entity.get(FloorReference.class).getFloorId();
		return StreamSupport
				.stream(engine.asSimulationEntity(floorId)
						.get(Floor.class).getEntitiesIn()
						.spliterator(),
						false)
				.filter(other -> other != entity)
				.filter(other -> other.get(Position2D.class).distance(
						entity.get(Position2D.class)) < radius)::iterator;
	}

	@Override
	public void update(float deltaTime)
	{

	}
	//
	// public Iterable<SimulationEntity> getAgentsInRange(CoordinateHolder l,
	// double range)
	// {
	// return l.getFloor().getAgentsInRange((int) (l.getX() - range),
	// (int) (l.getY() - range), (int) (l.getX() + range),
	// (int) (l.getY() + range));
	//
	// }
	//
	// public Iterable<LowLevelAgent> getAgentsInRange(LowLevelAgent a,
	// double range)
	// {
	// return Filters.allExcept(this.getAgentsInRange(a.getLocation(), range),
	// a);
	// }
	//
	// public CoordinateHolder getRandomRoom()
	// {
	// return this.building.getRandomRoom();
	// }
	//
	// public Location getNamedLocation(String name)
	// {
	// return this.building.getNamedLocation(name);
	// }

}

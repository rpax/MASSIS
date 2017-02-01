package com.massisframework.massis.model.systems;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.Floor;
import com.massisframework.massis.model.components.FloorReference;
import com.massisframework.massis.model.components.FollowTarget;
import com.massisframework.massis.model.components.MovingTo;
import com.massisframework.massis.model.components.TransformComponent;
import com.massisframework.massis.pathfinding.straightedge.SEPathFinder;
import com.massisframework.massis.sim.ecs.CollectionsFactory;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntity;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntityData;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntitySet;
import com.massisframework.massis.util.geom.CoordinateHolder;
import com.massisframework.massis.util.geom.KVector;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class PathFindingSystem implements SimulationSystem {

	private Map<Long, SEPathFinder> pathFinders;

	@Inject
	private SimulationEntityData ed;
	// @FilterParams(all = Floor.class)
	private SimulationEntitySet floors;

	// @FilterParams(
	// all = { FollowTarget.class,
	// FloorReference.class,
	// Position2D.class })

	private SimulationEntitySet followers;

	@Override
	public void initialize()
	{
		this.pathFinders = CollectionsFactory.newMap(Long.class,
				SEPathFinder.class);
		this.floors = this.ed.createEntitySet(Floor.class);
		this.followers = this.ed.createEntitySet(FollowTarget.class,
				FloorReference.class,
				TransformComponent.class);
	}

	@Override
	public void update(float deltaTime)
	{
		if (this.floors.applyChanges())
		{
			for (SimulationEntity e : this.floors.getAddedEntities())
			{
				this.pathFinders.put(e.getId().getId(),
						new SEPathFinder(this.ed, e.getId()));

			}
		}

		if (this.followers.applyChanges())
		{
			for (SimulationEntity e : this.followers.getAddedEntities())
			{

				CoordinateHolder target = e.getC(FollowTarget.class)
						.getTarget();

				long floorId = e.getC(FloorReference.class).getFloorId();
				SEPathFinder pF = this.pathFinders.get(floorId);
				if (pF == null)
				{
					Logger.getLogger(getClass().getName())
							.warning("Pathfinder not ready");
					continue;
				}

				TransformComponent tr = e.getC(TransformComponent.class);
				List<CoordinateHolder> path = pF
						.findPath(new KVector(tr.getX(), tr.getY()), target);

				e.addC(MovingTo.class)
						.set(MovingTo::setTarget, path.get(1))
						.commit();
			}
		}

	}

}

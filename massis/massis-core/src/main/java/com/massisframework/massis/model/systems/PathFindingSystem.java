package com.massisframework.massis.model.systems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.massisframework.massis.model.components.Floor;
import com.massisframework.massis.model.components.FloorReference;
import com.massisframework.massis.model.components.FollowTarget;
import com.massisframework.massis.model.components.MovingTo;
import com.massisframework.massis.model.components.Position2D;
import com.massisframework.massis.pathfinding.straightedge.SEPathFinder;
import com.massisframework.massis.sim.FilterParams;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.ComponentFilterBuilder;
import com.massisframework.massis.sim.ecs.OLDSimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.util.geom.CoordinateHolder;
import com.massisframework.massis.util.geom.KVector;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import straightedge.geom.KPoint;

public class PathFindingSystem implements SimulationSystem {

	private Map<Integer, SEPathFinder> pathFinders;

	@Inject
	private SimulationEngine<?> engine;
	private List<OLDSimulationEntity<?>> entities = new ArrayList<>();
	@FilterParams(all = Floor.class)
	private ComponentFilter floorFilter;
	@FilterParams(
			all = { FollowTarget.class,
					FloorReference.class,
					Position2D.class })

	private ComponentFilter followersFilter;

	@Inject
	private Provider<ComponentFilterBuilder> cFBuilder;

	@Override
	public void initialize()
	{
		this.pathFinders = new Int2ObjectOpenHashMap<>();

	}

	@Override
	public void update(float deltaTime)
	{
		for (OLDSimulationEntity<?> e : this.engine.getEntitiesFor(floorFilter,
				entities))
		{
			int floorId = e.getId();
			if (!this.pathFinders.containsKey(e.getId()))
			{
				SEPathFinder pF = new SEPathFinder(cFBuilder, engine, floorId);
				this.pathFinders.put(floorId, pF);
			}

		}
		for (OLDSimulationEntity<?> e : this.engine.getEntitiesFor(followersFilter,
				entities))
		{

			CoordinateHolder target = e.get(FollowTarget.class)
					.getTarget();

			long floorId = e.get(FloorReference.class)
					.getFloorId();
			SEPathFinder pF = this.pathFinders.get(floorId);
			e.get(FollowTarget.class)
					.setTarget(new KVector(pF.getNearestPointOutsideOfObstacles(
							new KPoint(target.getX(), target.getY()))));

			List<CoordinateHolder> path = pF.findPath(
					new KVector(e.get(Position2D.class).getXY()),
					target);
			e.addComponent(MovingTo.class).setTarget(path.get(1));
		}

	}

}

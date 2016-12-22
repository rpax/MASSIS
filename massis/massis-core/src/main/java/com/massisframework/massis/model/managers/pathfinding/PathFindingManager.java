package com.massisframework.massis.model.managers.pathfinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.components.Location;
import com.massisframework.massis.pathfinding.straightedge.SEPathFinder;
import com.massisframework.massis.pathfinding.straightedge.SimulationPathFinder;
import com.massisframework.massis.sim.engine.SimulationEngine;
import com.massisframework.massis.sim.engine.SimulationSystem;

import straightedge.geom.KPoint;

public class PathFindingManager implements SimulationSystem   {

	private Map<Floor, SimulationPathFinder> pathFinders;
	private SimulationEngine engine;

	public PathFindingManager()
	{
		this.pathFinders = new HashMap<>();
	}
	
	public List<KPoint> findPath(
			final Location from,
			final Location toLoc)
	{
		SimulationPathFinder pathfinder = getPathFinderOf(from);
		return pathfinder.findPath(from,toLoc);
	}
	private SimulationPathFinder getPathFinderOf(Floor floor){
		SimulationPathFinder pF = this.pathFinders.get(floor);
		if (pF == null)
		{
			pF = new SEPathFinder(floor,this.engine);
			this.pathFinders.put(floor, pF);
		}
		return pF;
	}
	private SimulationPathFinder getPathFinderOf(Location from)
	{
		return getPathFinderOf(from.getFloor());
	}

	@Override
	public void update(float deltaTime)
	{
		
	}

	@Override
	public void addedToEngine(SimulationEngine simEngine)
	{
		this.engine=simEngine;
	}

	@Override
	public void removedFromEngine(SimulationEngine simEngine)
	{
	}

	

}

package com.massisframework.massis.model.systems;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.MovingTo;
import com.massisframework.massis.model.components.Position2D;
import com.massisframework.massis.model.components.Velocity;
import com.massisframework.massis.sim.FilterParams;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.OLDSimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.util.geom.CoordinateHolder;
import com.massisframework.massis.util.geom.KVector;

public class MovementSystem implements SimulationSystem {

	@FilterParams(all = { MovingTo.class, Velocity.class, Position2D.class })
	private ComponentFilter movingFilter;

	private List<OLDSimulationEntity<?>> entities;
	@Inject
	SimulationEngine<?> engine;

	@Override
	public void initialize()
	{
		this.entities = new ArrayList<>();
	}

	@Override
	public void update(float deltaTime)
	{
		for (OLDSimulationEntity<?> e : this.engine.getEntitiesFor(movingFilter,
				this.entities))
		{

			CoordinateHolder target = e.get(MovingTo.class)
					.getTarget();
			
			// followPath
			KVector newVel = new KVector(target.getX(), target.getY()).sub(
					e.get(Position2D.class).getXY())
					.normalize()
					.mult(100);
			e.get(Velocity.class).setValue(newVel);
		}
	}
}

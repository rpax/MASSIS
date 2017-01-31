package com.massisframework.massis.model.systems;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.massisframework.massis.model.components.Position2D;
import com.massisframework.massis.model.components.Velocity;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.ComponentFilterBuilder;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.util.geom.KVector;

public class VelocitySystem implements SimulationSystem {

	@Inject
	private Provider<ComponentFilterBuilder> cfBuilderProvider;
	@Inject
	private SimulationEngine engine;

	private ComponentFilter filter;
	private List<SimulationEntity> entities = new ArrayList<>();

	@Override
	public void initialize()
	{
		this.filter = cfBuilderProvider.get().all(
				Position2D.class,
				Velocity.class).get();
	}

	@Override
	public void update(float deltaTime)
	{
		engine.getEntitiesFor(filter, entities);

		for (SimulationEntity<?> se : entities)
		{
			// final SteeringBehavior steeringBeh = se
			// .getComponent(SteeringComponent.class)
			// .getSteeringBehavior();
			/*
			 * Get forces
			 */
			// final KVector force = steeringBeh.steer();
			/*
			 * Apply them and proceed to move
			 */
			// applySteeringForcesAndMove(se, force);
			Position2D pos = se.get(Position2D.class);
			Velocity vel = se.get(Velocity.class);
			
			KVector newPos = vel
					.getValue()
					.copy().mult(deltaTime)
					.add(pos.getXY());
			pos.set(newPos.x, newPos.y);
		}
	}
	// private void fixInvalidLocation(DefaultAgent agent, Location toLoc) {
	// logger.log(Level.INFO, "Fixing location for {0}.", agent);
	// // 1. Quitamos todo.vehicle.getVelocity().limit(vehicle.getMaxSpeed());
	// removeFromCache(agent);
	// // 2. Se busca la localizacion valida mas cercana
	// SimRoom sr = agent.getRoom();
	// if (sr == null) {
	// System.err.println("Last Known room not found for agent " + agent);
	// } else {
	// // A boleo. Un punto cualquiera de la habitacion, y le mandamos
	// // hacia alla.
	// // System.err.println("Path finding error fix " + agent);
	// Path path = agent.getLocation().getFloor()
	// .findPath(agent.getRoom().getRandomLoc(), toLoc);
	// if (path != null) {
	// path.getPoints().add(0, agent.getXY());
	// }
	// this.paths.put(agent, path);
	// this.targets.put(agent, new Location(toLoc));
	// }
	//
	// }

	/**
	 * Applies the forces given to the current position and velocity of the
	 * agent, producing a new position and a new velocity. The agent is updated
	 * and moved accordingly
	 *
	 * @param vehicle
	 *            the agent to move
	 * @param forces
	 *            the forces applied to that agent
	 */
	private void applySteeringForcesAndMove(
			SimulationEntity<?> vehicle,
			KVector forces)
	{

		// SteeringComponent s = vehicle.getComponent(SteeringComponent.class);
		// Velocity velC = vehicle.getComponent(Velocity.class);
		// Position2D position2D = vehicle.getComponent(Position2D.class);
		//
		// forces.mult(s.getMaxForce());
		// final KVector steering = KVector.limit(forces, s.getMaxForce());
		// // steering = steering / mass
		// s.setAcceleration(steering);
		// final KVector velocity = KVector.limit(
		// KVector.add(steering, velC.getValue()),
		// s.getMaxSpeed());
		// velC.setValue(velocity);
		//
		// final KVector position = new KVector(position2D.getX(),
		// position2D.getY()).add(velC.getValue());
		//
		// // final Location newLocation = new Location(position,
		// // vehicle.getLocation().getFloor());
		//
		// position2D.set(position.x, position.y);

		// vehicle.moveTo(newLocation);

	}

}

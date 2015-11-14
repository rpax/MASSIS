package com.massisframework.massis.model.managers.movement;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.building.SimRoom;
import com.massisframework.massis.model.building.Teleport;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.managers.movement.steering.SteeringBehavior;
import com.massisframework.massis.pathfinding.straightedge.FindPathResult;
import com.massisframework.massis.pathfinding.straightedge.FindPathResult.PathFinderErrorReason;
import com.massisframework.massis.util.geom.KVector;

import straightedge.geom.KPoint;

/**
 * Manager in charge of controlling the movements of the agents.
 *
 * @author rpax
 *
 */
public class MovementManager {

	private static Logger logger = Logger
			.getLogger(MovementManager.class.getName());

	

	/**
	 * Approachs an agent to the desired location
	 *
	 * @param vehicle
	 *            the agent
	 * @param toLoc
	 *            the target location
	 * @param range
	 *            the range for checking if it is in location
	 */
	public void approachTo(final DefaultAgent vehicle, final Location toLoc,
			float range, ApproachCallback callback) {

//		if (checkForArrival(vehicle, toLoc, range)) {
//			callback.onResult(ApproachResult.ALREADY_IN_LOCATION);
//			return;
//		}

		/*
		 * Retrieval of the agent's steering behavior
		 */
		SteeringBehavior steeringBeh = vehicle.getSteeringBehavior();
		/*
		 * Get forces
		 */
		KVector force = steeringBeh.steer();
		/*
		 * Apply them and proceed to move
		 */
		applySteeringForcesAndMove(vehicle, force);

	}

	private boolean checkForArrival(DefaultAgent vehicle, Location toLoc,
			float range) {
		return (vehicle.getLocation().getFloor() == toLoc.getFloor() && (vehicle
				.getLocation().distance2D(toLoc) < vehicle.getMaxSpeed()
				|| vehicle.getLocation().distance2D(toLoc) < range));
	}

//	private void fixInvalidLocation(DefaultAgent agent, Location toLoc) {
//		logger.log(Level.INFO, "Fixing location for {0}.", agent);
//		// 1. Quitamos todo.vehicle.getVelocity().limit(vehicle.getMaxSpeed());
//		removeFromCache(agent);
//		// 2. Se busca la localizacion valida mas cercana
//		SimRoom sr = agent.getRoom();
//		if (sr == null) {
//			System.err.println("Last Known room not found for agent " + agent);
//		} else {
//			// A boleo. Un punto cualquiera de la habitacion, y le mandamos
//			// hacia alla.
//			// System.err.println("Path finding error fix " + agent);
//			Path path = agent.getLocation().getFloor()
//					.findPath(agent.getRoom().getRandomLoc(), toLoc);
//			if (path != null) {
//				path.getPoints().add(0, agent.getXY());
//			}
//			this.paths.put(agent, path);
//			this.targets.put(agent, new Location(toLoc));
//		}
//
//	}

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
	private void applySteeringForcesAndMove(DefaultAgent vehicle,
			KVector forces) {

		forces.mult(vehicle.getMaxForce());
		KVector steering = KVector.limit(forces, vehicle.getMaxForce());
		// steering = steering / mass
		vehicle.setAcceleration(steering);
		KVector velocity = KVector.limit(
				KVector.add(steering, vehicle.getVelocity()),
				vehicle.getMaxSpeed());
		vehicle.setVelocity(velocity);
		KVector position = KVector.add(vehicle.getXY(), vehicle.getVelocity());

		Location newLocation = new Location(position,
				vehicle.getLocation().getFloor());

		vehicle.moveTo(newLocation);

	}

}

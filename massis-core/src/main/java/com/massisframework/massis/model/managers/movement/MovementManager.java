package com.massisframework.massis.model.managers.movement;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.building.SimRoom;
import com.massisframework.massis.model.building.Teleport;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.managers.movement.steering.SteeringBehavior;
import com.massisframework.massis.util.geom.KVector;

import straightedge.geom.KPoint;

/**
 * Manager in charge of controlling the movements of the agents.
 *
 * @author rpax
 *
 */
public class MovementManager {

    /**
     * Cached paths
     */
    private final HashMap<DefaultAgent, Path> paths = new HashMap<>();
    /**
     * Cached targets
     */
    private final HashMap<DefaultAgent, Location> targets = new HashMap<>();
    private static Logger logger = Logger.getLogger(
            MovementManager.class.getName());

    /**
     * Approachs an agent to the desired location
     *
     * @param vehicle the agent
     * @param toLoc the target location
     * @return if it is in the location already
     */
    public boolean approachTo(DefaultAgent vehicle, Location toLoc)
    {
        if (checkForArrival(vehicle, toLoc))
        {
            return true;
        }
        if (!toLoc.equals(this.targets.get(vehicle)))
        {
            this.removeFromCache(vehicle);
        }
        try
        {
            /*
             * Path retrieval. If there is no path, a new one is requested
             */
            Path path = this.paths.get(vehicle);
            if (path == null)
            {

                path = vehicle.getLocation().getFloor()
                        .findPath(vehicle.getLocation(), toLoc);

                if (path != null)
                {
                    this.paths.put(vehicle, path);
                    this.targets.put(vehicle, new Location(toLoc));
                } else
                {
                    /*
                     * If everything goes wrong, try to fix it
                     */
                    fixInvalidLocation(vehicle, toLoc);

                    return false;
                }

            }
            /*
             * If the agent is on a teleport, and wants to be teleported, it is
             * teleported
             */
            if (path.isInTargetTeleport(vehicle))
            {
                // Si si que tiene camino, y resulta que esta en el teleport
                // adecuado, se le teletransporta
                logger.log(Level.INFO,
                        "Vehicle is in target teleport.{0}.",
                        new Object[]
                {
                    path.getTargetTeleport()
                });

                vehicle.getVelocity().mult(0);
                final Teleport connectedTeleport=path.getTargetTeleport()
                        .getConnection();
                
                logger.log(Level.INFO,
                        "Moving vehicle to connected teleport: {0}.",connectedTeleport);
                vehicle.moveTo(connectedTeleport.getLocation());
                removeFromCache(vehicle);
                vehicle.clearCache();
                for (SimRoom sr : path.getTargetTeleport().getConnection()
                        .getLocation().getFloor().getRooms())
                {
                    if (sr.getPolygon().intersects(vehicle.getPolygon()))
                    {
                        vehicle.setLastKnowRoom(sr);
                        break;
                    }
                }
                /*
                 * Remove from cache : Now the agent is in another floor.
                 */

               // return this.approachTo(vehicle, toLoc);
                return false;
            }
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

        } catch (Exception e1)
        {
            e1.printStackTrace();
        }
        if (checkForArrival(vehicle, toLoc))
        {
            return true;
        }

        return false;
    }

    private boolean checkForArrival(DefaultAgent vehicle, Location toLoc)
    {
        return (vehicle.getLocation().getFloor() == toLoc.getFloor() && (vehicle
                .getLocation().distance2D(toLoc) < vehicle.getMaxSpeed() || vehicle
                .getLocation().distance2D(toLoc) < vehicle.getPolygon()
                .getRadius()));
    }

    private void fixInvalidLocation(DefaultAgent agent, Location toLoc)
    {
        logger.log(Level.INFO,
                        "Fixing location for {0}.",agent);
        // 1. Quitamos todo.vehicle.getVelocity().limit(vehicle.getMaxSpeed());
        removeFromCache(agent);
        // 2. Se busca la localizacion valida mas cercana
        SimRoom sr = agent.getRoom();
        if (sr == null)
        {
            System.err.println("Last Known room not found for agent " + agent);
        } else
        {
            // A boleo. Un punto cualquiera de la habitacion, y le mandamos
            // hacia alla.
            // System.err.println("Path finding error fix " + agent);
            Path path = agent.getLocation().getFloor()
                    .findPath(agent.getRoom().getRandomLoc(), toLoc);
            if (path != null)
            {
                path.getPoints().add(0, agent.getXY());
            }
            this.paths.put(agent, path);
            this.targets.put(agent, new Location(toLoc));
        }

    }

    private void removeFromCache(DefaultAgent v)
    {
        this.paths.remove(v);
        this.targets.remove(v);
    }

    /**
     * Applies the forces given to the current position and velocity of the
     * agent, producing a new position and a new velocity. The agent is updated
     * and moved accordingly
     *
     * @param vehicle the agent to move
     * @param forces the forces applied to that agent
     */
    private void applySteeringForcesAndMove(DefaultAgent vehicle, KVector forces)
    {

        forces.mult(vehicle.getMaxForce());
        KVector steering = KVector.limit(forces, vehicle.getMaxForce());
        // steering = steering / mass
        vehicle.setAcceleration(steering);
        KVector velocity = KVector.limit(
                KVector.add(steering, vehicle.getVelocity()),
                vehicle.getMaxSpeed());
        vehicle.setVelocity(velocity);
        KVector position = KVector.add(vehicle.getXY(), vehicle.getVelocity());

        Location newLocation = new Location(position, vehicle.getLocation()
                .getFloor());

        vehicle.moveTo(newLocation);

    }

    /**
     * Returns the cached path of the agent provided
     *
     * @param v the agent
     * @return the agent's path, an empty list if has no path.
     */
    public List<KPoint> getPathOf(DefaultAgent v)
    {
        Path path = this.paths.get(v);
        if (path == null || path.isEmpty())
        {
            return Collections.emptyList();
        }
        return path.getPoints();
    }
}

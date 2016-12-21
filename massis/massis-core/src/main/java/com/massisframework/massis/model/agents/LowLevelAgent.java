/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.massis.model.agents;

import java.awt.Shape;
import java.util.Collection;

import com.massisframework.massis.model.building.ISimulationObject;
import com.massisframework.massis.model.building.SimRoom;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.location.SimLocation;
import com.massisframework.massis.model.managers.movement.ApproachCallback;
import com.massisframework.massis.model.managers.pathfinding.PathFollower;
import com.massisframework.massis.util.Indexable;
import com.massisframework.massis.util.SimObjectProperty;
import com.massisframework.massis.util.geom.CoordinateHolder;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;
import straightedge.geom.PolygonHolder;
import straightedge.geom.vision.Occluder;

/**
 *
 * @author Rafael Pax
 */
public interface LowLevelAgent extends  PathFollower,Indexable,SteeringCapable,ISimulationObject {

    /**
     * Tries to approach to an specific {@link Location} in the building.
     * <p>This method <b>does not</b> "Move" the agent to one location to
     * another instantly. Instead, the agent tries to be closer to the location
     * given, avoiding obstacles.</p>
     * <p>
     * For moving the agent to one location <i>instantly</i> the method
     * {@link #moveTo(rpax.massis.model.location.Location)} should be used
     * instead
     *
     * @param location the target {@link Location}
     * @return if the agent has reached the location or not
     */
    public void approachTo(Location location,ApproachCallback callback);
    /**
     * Returns the agents lying in the area defined by the location of the agent
     * and the radius defined by the range provided. It is an alternative method
     * to {@link #getAgentsInVisionRadio()}. The former is preferred to be used,
     * but this method can be useful for creating new low-level operations.
     *
     * @param range the radius of the circle.
     * @return
     */
    public Iterable<LowLevelAgent> getAgentsInRange(double range);
    /**
     * Returns the <i>visible</i> agents in the vision radio of this agent.
     *
     * @return the agents in the vision radio of this agent.
     */
    public Iterable<? extends LowLevelAgent> getAgentsInVisionRadio();
    /**
     * Returns the current room of the agent
     *
     * @return The room where the agent is
     */
    public CoordinateHolder getRoom();
    /**
     * Retrieves the agents in the current room of the agent. Equivalent method
     * to the call {@link SimRoom#getPeopleIn()}
     *
     * @return the agents in the current room of the agent.
     */
    public Collection<? extends LowLevelAgent> getAgentsInRoom();
    /**
     * Retrieves the vision radio of this agent
     *
     * @return the vision radio of this agent
     */
    public double getVisionRadio();

    /**
     * Retrieves the {@link Shape} that defines the vision radio shape of the
     * agent. It is different from {@link #getAgentsInVisionRadio() } due to
     * performance reasons. If the vision area of the agent is uniform (e.g not
     * <i>directed</i>), the formed method is preferred.
     *
     * @return the {@link Shape} of the vision polygon
     */
    public Shape getVisionRadioShape();

    /**
     * Returns if the agent it is in a door or not. Mostly used for performance
     * reasons. <p>Due to SweetHome3D internal representation of the building,
     * if the agent is under a door, exists the possibility that the agent it is
     * not located in any room. Although the method {@link #getRoom() } will
     * return the last room where the agent was, this method it is used in some
     * low-level operations</p>
     *
     * @return if the agent is in a door or not
     */
    public boolean isInDoorArea();

    /**
     * Returns if another low level agent is in the vision radio of this agent.
     * <p>This method is equivalent to calling {@link #getAgentsInVisionRadio()
     * } and checking if it is contained in that collection, but this method is
     * faster.
     * </p>
     *
     * @param other The agent to be checked
     * @return if the agent provided lies in the vision radio of this agent and
     * is in the same room of this agent
     */
    public boolean isObjectPerceived(LowLevelAgent other);

    /**
     * @param p the point to check
     * @return if the point is contained in the vision area
     */
    public boolean isPointContainedInVisionArea(KPoint p);

    /**
     * Moves instantly this agent to another point. (a.k.a <i>warping</i>).
     * <p>Does not take into account the velocity, location, floor or anything.
     * Just moves the agent instantly to the {@link Location} provided.
     *
     * @param other the location where the agent should be moved.
     */
    @Override
	public void moveTo(Location other);

    /**
     * Returns if this agent it is an obstacle or not.
     * <p> MASSIS does not take into account the elevation of the agents, so it
     * is possible that other agents try to avoid some elements of the
     * environment that they are not an obstacle (e.g a video camera)</p>
     *
     * @return if this agent it is an obstacle or not.
     */
    public boolean isObstacle();

    /**
     * Returns if this agent can move or not.
     * <p>Used for performance reasons. If the agent is marked in its metadata
     * with the flag {@link SimObjectProperty#IS_DYNAMIC} to {@code false},
     * pathfinding algorithms will consider this agent as an <i>static</i>
     * obstacle, increasing performance.</p>
     *
     * @return
     */
    public boolean isDynamic();

    /**
     * Retrieves the body radius of the agent.
     * <p>That is, the radio of the polygon representing this agent</p>
     *
     * @see Occluder
     * @see PolygonHolder
     * @see KPolygon
     * @return the body radius of this agent
     */
    public double getBodyRadius();

    /**
     * Returns if the agent is around a named location.
     * <p>A named location is an special element of the environment, that
     * represents an special point. (For example, an emergency exit). For
     * creating a Named location, the metadata of an element of the building
     * should contain the key {@link SimObjectProperty#POINT_OF_INTEREST}</p>
     *
     * @param name the name of this location
     * @param radiusWithin the radio for considering this agent in that
     * location. If the {@link SimLocation#distance2D(straightedge.geom.KPoint)}
     * is lower than {@code radiusWithIn}, the result will be {@code true},
     * being {@code false} otherwise.
     * @return
     */
    public boolean isInNamedLocation(String name, int radiusWithin);

    /**
     * Tries to execute the {@link #approachTo(rpax.massis.model.location.Location)
     * } to the location of a {@link NamedLocation}.
     * <p>The {@link NamedLocation} object is not needed, only its name.</p>
     *
     * @param name the name of the named location
     * @return if has reached the {@link SimLocation} of the
     * {@link NamedLocation} or not.
     */
    public void approachToNamedLocation(String name,ApproachCallback callback);

//    /**
//     * Creates a random target and tries to make the {@link #approachTo(rpax.massis.model.location.Location)
//     * } step for reaching it.
//     *
//     * @return The random target generated. It is returned because it might be
//     * useful for some High-Level operations.
//     */
//    public Location approachToRandomTarget();

    /**
     * Returns a dynamic property of the agent. <p> This properties can be used
     * for High-Level operations.</p>.
     *
     * @param propertyName a property of the agent
     * @return Its value. If the property is not present, {@code null} is
     * returned.
     */
    public Object getProperty(String propertyName);

    /**
     * Returns if the agent contains the provided property
     *
     * @param propertyName the property to be checked
     * @return if the agent contains that property or not.
     */
    public boolean hasProperty(String propertyName);

    /**
     * Sets a property to the agent's properties map.
     *
     * @param propertyName the name of the property
     * @param value the value of the given property
     */
    public void setProperty(String propertyName, Object value);

    /**
     * Removes a property from the agent's properties map.
     *
     * @param propertyName The property to be removed
     */
    public void removeProperty(String propertyName);

    /**
     * Used from the High-Level controller for storing a custom data structure.
     * <p>Every Low-Level agent can contain as much <b>one</b> High-Level
     * controller. The High Level controller should know what is storing there.
     * The usage of this object is similar to the {@code getUserData()} and
     * {@code setUserData()} of graphical libraries.</p>
     * <p>Could be set as a property of this agent, but it is done in this way
     * for clarity reasons</p>
     *
     * @return The high-Level data stored by the high-level behavior of the
     * agent
     */
    public Object getHighLevelData();

    /**
     * @see #getHighLevelData()
     * @param highLevelData the High-Level data of this agent
     */
    public void setHighLevelData(Object highLevelData);
    public CoordinateHolder getRandomRoom() ;
    
	public void setMaxSpeed(double maxspeed);
	public void setMaxForce(double maxforce);
}

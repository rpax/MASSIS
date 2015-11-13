/**
 *
 */
package com.massisframework.massis.model.building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.location.SimLocation;
import com.massisframework.massis.model.managers.AnimationManager;
import com.massisframework.massis.model.managers.EnvironmentManager;
import com.massisframework.massis.model.managers.movement.MovementManager;
import com.massisframework.massis.util.Indexable;
import com.massisframework.massis.util.SimObjectProperty;
import com.massisframework.massis.util.geom.CoordinateHolder;
import com.massisframework.massis.util.io.JsonState;
import com.massisframework.massis.util.io.Restorable;
import com.massisframework.massis.util.io.RestorableObserver;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;
import straightedge.geom.PolygonHolder;

/**
 * Basic element of the simulation. It is the result of processing the elements
 * of SweetHome3D.
 *
 * @author rpax
 *
 */
public abstract class SimulationObject implements PolygonHolder, Indexable, CoordinateHolder, LocationHolder, Restorable {

    private static final long serialVersionUID = 1L;
    /**
     * The id of this object
     */
    protected final int id;
    /**
     * Properties of this element.
     */
    private final Map<String, Object> properties = new HashMap<>();
    // Managers
    private final MovementManager movement;
    private final AnimationManager animation;
    private final EnvironmentManager environment;
    /**
     * Location of this object
     */
    private final SimLocation location;

    private ArrayList<RestorableObserver> restorableObservers = new ArrayList<>();

    /**
     * Main constructor
     *
     * @param metadata the SweetHome3D metadata of this element
     * @param location the location of this element
     * @param movementManager in charge of movement
     * @param animationManager in charge of animation
     * @param environment in charge of retrieving information from the
     * environment
     * @param resourcesFolder the resources folder
     */
    public SimulationObject(final Map<String, String> metadata,
            SimLocation location, MovementManager movementManager,
            AnimationManager animationManager, EnvironmentManager environment)
    {

        this.id = Integer.parseInt(metadata.get(SimObjectProperty.ID.toString()));
        this.movement = movementManager;
        this.animation = animationManager;
        this.environment = environment;
        this.location = location;
        this.getLocation().attach(this);
    }

    @Override
    public final SimLocation getLocation()
    {
        return this.location;
    }

    @Override
    public final int getID()
    {
        return this.id;
    }

    /**
     * Moves the agent to an specific location
     *
     * @param other the target location
     */
    public void moveTo(Location other)
    {
        this.location.translateTo(other);
        this.animate();
        //
        this.notifyChanged();

    }

    public void addRestorableObserver(RestorableObserver obs)
    {
        this.restorableObservers.add(obs);
    }

    public void removeRestorableObserver(RestorableObserver obs)
    {
        this.restorableObservers.remove(obs);
    }

    protected final void notifyChanged()
    {
        for (RestorableObserver restorableObserver : restorableObservers)
        {
            restorableObserver.notifyChange(this,this.getState());
        }
    }

    public void animate()
    {
        this.animation.animate(this);
    }

    @Override
    public final double getX()
    {
        return this.location.getX();
    }

    @Override
    public final double getY()
    {
        return this.location.getY();
    }

    @Override
    public final KPoint getXY()
    {

        return this.getPolygon().center;
    }

    /**
     * Returns the coordinates of this object.
     *
     * @param coord available 1D lenght 2 array
     * @return the same array, filled with the coordinates of this object
     */
    public double[] getXYCoordinates(final double[] coord)
    {
        coord[0] = this.location.getX();
        coord[1] = this.location.getY();
        return coord;
    }

    @Override
    public final KPolygon getPolygon()
    {
        return this.location.getPolygon();
    }

    /**
     * TODO rpax. Remove it from here.
     *
     * @return the connectors on the floor of this agent
     */
    public List<RoomConnector> getRoomsConnectorsInSameFloor()
    {
        return this.getLocation().getFloor().getRoomConnectors();
    }

//    public void step()
//    {
//        // nothing by default
//    }
//    @Override
//    public void stop()
//    {
//    }
    public Object getProperty(String propertyName)
    {
        if (!this.properties.containsKey(propertyName))
        {
            return null;
        }
        return this.properties.get(propertyName);
    }

    public boolean hasProperty(String propertyName)
    {
        return this.properties.containsKey(propertyName);
    }

    public void setProperty(String propertyName, Object value)
    {
        this.properties.put(propertyName, value);
    }

    public void removeProperty(String propertyName)
    {
        this.properties.remove(propertyName);
    }

    @Override
    public String toString()
    {
        return "[SimulationObject#" + this.getID() + " {" + this.location
                + "}]";
    }

    public double getAngle()
    {
        return this.location.getAngle();
    }

    @Override
    public JsonState<Building> getState()
    {
        return new SimulationObjectState(this);
    }

    @Override
    public int hashCode()
    {
        return this.id;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof SimulationObject))
        {
            return false;
        }

        SimulationObject other = (SimulationObject) obj;
        if (id != other.id)
        {
            return false;
        }
        return true;
    }

    private static class SimulationObjectState implements JsonState<Building> {

        protected int id;
        protected HashMap<String, Object> properties;
        protected JsonState<Building> locationState;

        public SimulationObjectState(SimulationObject obj)
        {
            this.id = obj.id;
            this.properties = new HashMap<>(obj.properties);
            this.locationState = obj.getLocation().getState();
        }

        @Override
        public SimulationObject restore(Building building)
        {
            SimulationObject simObj = building.getSimulationObject(id);
            for (Entry<String, Object> entry : this.properties.entrySet())
            {
                simObj.setProperty(entry.getKey(), entry.getValue());
            }
            locationState.restore(building);
            simObj.animate();
            return simObj;
        }
    }

    public MovementManager getMovementManager()
    {
        return movement;
    }

    public EnvironmentManager getEnvironment()
    {
        return environment;
    }
}

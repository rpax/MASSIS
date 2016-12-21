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
import com.massisframework.massis.model.managers.pathfinding.PathFindingManager;
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
public abstract class SimulationObject implements PolygonHolder, Indexable,
		CoordinateHolder, LocationHolder, Restorable,Movable, ISimulationObject {

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
	protected final PathFindingManager pathManager;
	/**
	 * Location of this object
	 */
	private final SimLocation location;

	private final ArrayList<RestorableObserver> restorableObservers = new ArrayList<>();

	/**
	 * Main constructor
	 *
	 * @param metadata
	 *            the SweetHome3D metadata of this element
	 * @param location
	 *            the location of this element
	 * @param movementManager
	 *            in charge of movement
	 * @param animationManager
	 *            in charge of animation
	 * @param environment
	 *            in charge of retrieving information from the environment
	 * 
	 * @param pathManager
	 *            the pathfinding manager
	 */
	public SimulationObject(final Map<String, String> metadata,
			SimLocation location, MovementManager movementManager,
			AnimationManager animationManager, EnvironmentManager environment,
			PathFindingManager pathManager) {

		this.id = Integer
				.parseInt(metadata.get(SimObjectProperty.ID.toString()));
		this.movement = movementManager;
		this.animation = animationManager;
		this.environment = environment;
		this.location = location;
		this.pathManager = pathManager;
		this.getLocation().attach(this);
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#getLocation()
	 */
	@Override
	public final SimLocation getLocation() {
		return this.location;
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#getID()
	 */
	@Override
	public final int getID() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#moveTo(com.massisframework.massis.model.location.Location)
	 */
	@Override
	public void moveTo(Location other) {
		this.location.translateTo(other);
		this.animate();
		//
		this.notifyChanged();

	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#addRestorableObserver(com.massisframework.massis.util.io.RestorableObserver)
	 */
	@Override
	public void addRestorableObserver(RestorableObserver obs) {
		this.restorableObservers.add(obs);
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#removeRestorableObserver(com.massisframework.massis.util.io.RestorableObserver)
	 */
	@Override
	public void removeRestorableObserver(RestorableObserver obs) {
		this.restorableObservers.remove(obs);
	}

	protected final void notifyChanged() {
		for (final RestorableObserver restorableObserver : this.restorableObservers) {
			restorableObserver.notifyChange(this, this.getState());
		}
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#animate()
	 */
	@Override
	public void animate() {
		this.animation.animate(this);
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#getX()
	 */
	@Override
	public final double getX() {
		return this.location.getX();
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#getY()
	 */
	@Override
	public final double getY() {
		return this.location.getY();
	}

//	@Override
	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#getXY()
	 */
	@Override
	public final KPoint getXY() {

		return this.getPolygon().center;
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#getXYCoordinates(double[])
	 */
	@Override
	public double[] getXYCoordinates(final double[] coord) {
		coord[0] = this.location.getX();
		coord[1] = this.location.getY();
		return coord;
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#getPolygon()
	 */
	@Override
	public final KPolygon getPolygon() {
		return this.location.getPolygon();
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#getRoomsConnectorsInSameFloor()
	 */
	@Override
	public List<RoomConnector> getRoomsConnectorsInSameFloor() {
		return this.getLocation().getFloor().getRoomConnectors();
	}

	// public void step()
	// {
	// // nothing by default
	// }
	// @Override
	// public void stop()
	// {
	// }
	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#getProperty(java.lang.String)
	 */
	@Override
	public Object getProperty(String propertyName) {
		if (!this.properties.containsKey(propertyName)) {
			return null;
		}
		return this.properties.get(propertyName);
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#hasProperty(java.lang.String)
	 */
	@Override
	public boolean hasProperty(String propertyName) {
		return this.properties.containsKey(propertyName);
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#setProperty(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setProperty(String propertyName, Object value) {
		this.properties.put(propertyName, value);
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#removeProperty(java.lang.String)
	 */
	@Override
	public void removeProperty(String propertyName) {
		this.properties.remove(propertyName);
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#toString()
	 */
	@Override
	public String toString() {
		return "[SimulationObject#" + this.getID() + " {" + this.location
				+ "}]";
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#getAngle()
	 */
	@Override
	public double getAngle() {
		return this.location.getAngle();
	}

	

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#getState()
	 */
	@Override
	public JsonState<Building> getState() {
		return new SimulationObjectState(this);
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ISimulationObject)) {
			return false;
		}

		final SimulationObject other = (SimulationObject) obj;
		if (this.id != other.id) {
			return false;
		}
		return true;
	}

	private static class SimulationObjectState implements JsonState<Building> {

		protected int id;
		protected HashMap<String, Object> properties;
		protected JsonState<Building> locationState;

		public SimulationObjectState(SimulationObject obj) {
			this.id = obj.id;
			this.properties = new HashMap<>(obj.properties);
			this.locationState = obj.getLocation().getState();
		}

		@Override
		public ISimulationObject restore(Building building) {
			final ISimulationObject simObj = building.getSimulationObject(this.id);
			for (final Entry<String, Object> entry : this.properties.entrySet()) {
				simObj.setProperty(entry.getKey(), entry.getValue());
			}
			this.locationState.restore(building);
			simObj.animate();
			return simObj;
		}
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#getMovementManager()
	 */
	@Override
	public MovementManager getMovementManager() {
		return this.movement;
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#getEnvironment()
	 */
	@Override
	public EnvironmentManager getEnvironment() {
		return this.environment;
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimulationObject#getPathManager()
	 */
	@Override
	public PathFindingManager getPathManager() {
		return this.pathManager;
	}
}

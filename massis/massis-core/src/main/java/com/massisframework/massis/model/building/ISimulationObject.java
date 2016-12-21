package com.massisframework.massis.model.building;

import java.util.Collection;
import java.util.List;

import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.location.SimLocation;
import com.massisframework.massis.model.managers.EnvironmentManager;
import com.massisframework.massis.model.managers.movement.MovementManager;
import com.massisframework.massis.model.managers.pathfinding.PathFindingManager;
import com.massisframework.massis.util.io.JsonState;
import com.massisframework.massis.util.io.RestorableObserver;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

public interface ISimulationObject {

	SimLocation getLocation();

	int getID();

	/**
	 * Moves the agent to an specific location
	 *
	 * @param other
	 *            the target location
	 */
	void moveTo(Location other);

	void addRestorableObserver(RestorableObserver obs);

	void removeRestorableObserver(RestorableObserver obs);

	void animate();

	double getX();

	double getY();

	//	@Override
	KPoint getXY();

	/**
	 * Returns the coordinates of this object.
	 *
	 * @param coord
	 *            available 1D lenght 2 array
	 * @return the same array, filled with the coordinates of this object
	 */
	double[] getXYCoordinates(double[] coord);

	KPolygon getPolygon();

	/**
	 * TODO rpax. Remove it from here.
	 *
	 * @return the connectors on the floor of this agent
	 */
	List<RoomConnector> getRoomsConnectorsInSameFloor();

	// public void step()
	// {
	// // nothing by default
	// }
	// @Override
	// public void stop()
	// {
	// }
	Object getProperty(String propertyName);
	public Collection<String> getPropertyNames();
	boolean hasProperty(String propertyName);

	void setProperty(String propertyName, Object value);

	void removeProperty(String propertyName);

	String toString();

	double getAngle();

	JsonState<Building> getState();

	int hashCode();

	boolean equals(Object obj);

	MovementManager getMovementManager();

	EnvironmentManager getEnvironment();

	PathFindingManager getPathManager();

}
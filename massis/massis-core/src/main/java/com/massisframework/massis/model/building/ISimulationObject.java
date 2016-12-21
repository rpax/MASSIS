package com.massisframework.massis.model.building;

import java.util.Collection;
import java.util.List;

import com.massisframework.massis.model.managers.EnvironmentManager;
import com.massisframework.massis.model.managers.movement.MovementManager;
import com.massisframework.massis.model.managers.pathfinding.PathFindingManager;
import com.massisframework.massis.util.Indexable;
import com.massisframework.massis.util.geom.CoordinateHolder;
import com.massisframework.massis.util.io.JsonState;
import com.massisframework.massis.util.io.Restorable;
import com.massisframework.massis.util.io.RestorableObserver;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

public interface ISimulationObject extends Restorable,CoordinateHolder,Indexable,Movable,LocationHolder{


	void addRestorableObserver(RestorableObserver obs);

	void removeRestorableObserver(RestorableObserver obs);

	void animate();

	//	@Override
	KPoint getXY();


	KPolygon getPolygon();

	/**
	 * TODO rpax. Remove it from here.
	 *
	 * @return the connectors on the floor of this agent
	 */
	List<RoomConnector> getRoomsConnectorsInSameFloor();

	Object getProperty(String propertyName);
	public Collection<String> getPropertyNames();
	boolean hasProperty(String propertyName);

	void setProperty(String propertyName, Object value);

	void removeProperty(String propertyName);


	double getAngle();

	JsonState<Building> getState();

	MovementManager getMovementManager();

	EnvironmentManager getEnvironment();

	PathFindingManager getPathManager();

}
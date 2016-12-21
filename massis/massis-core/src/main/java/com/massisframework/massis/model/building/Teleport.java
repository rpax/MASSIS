package com.massisframework.massis.model.building;

import java.util.List;

import com.massisframework.massis.model.components.Location;
import com.massisframework.massis.model.managers.pathfinding.PathFollower;
import com.massisframework.massis.sim.SimulationEntity;
import com.massisframework.massis.util.io.JsonState;

public interface Teleport extends RoomConnector, WayPoint, LocationHolder {

	/**
	 *
	 */
	byte START = 0;
	byte END = 1;

	boolean isInTeleport(Location loc);

	String getName();

	SimulationEntity getConnection();

	public void setConnection(SimulationEntity connection);

	byte getType();

//	List<SimulationEntity> getConnectedRooms();

	int getDistanceToFloor(Floor f);

	String toString();

	JsonState<Building> getState();

	boolean canExecuteWayPointAction(PathFollower pf);

	boolean executeWayPointAction(PathFollower vehicle);

	public void setDistanceToFloor(Floor f, int distance);
}
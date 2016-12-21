package com.massisframework.massis.model.building;

import java.util.List;

import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.managers.pathfinding.PathFollower;
import com.massisframework.massis.util.io.JsonState;

public interface ITeleport extends RoomConnector, WayPoint, LocationHolder {

	/**
	 *
	 */
	byte START = 0;
	byte END = 1;

	boolean isInTeleport(Location loc);

	String getName();

	ITeleport getConnection();

	void setConnection(ITeleport connection);

	byte getType();

	List<SimRoom> getConnectedRooms();

	int getDistanceToFloor(Floor f);

	String toString();

	JsonState<Building> getState();

	boolean canExecuteWayPointAction(PathFollower pf);

	boolean executeWayPointAction(PathFollower vehicle);

	public void setDistanceToFloor(Floor f, int distance);
}
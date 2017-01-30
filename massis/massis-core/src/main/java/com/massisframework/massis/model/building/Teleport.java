package com.massisframework.massis.model.building;

import java.util.List;

public interface Teleport extends RoomConnector, WayPoint {

	/**
	 *
	 */
	byte START = 0;
	byte END = 1;

//	boolean isInTeleport(Location loc);

	String getName();

	Teleport getConnection();

	void setConnection(Teleport connection);

	byte getType();

	List<SimRoom> getConnectedRooms();

	int getDistanceToFloor(Floor f);

	String toString();

	

	public void setDistanceToFloor(Floor f, int distance);
}
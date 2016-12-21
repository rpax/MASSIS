package com.massisframework.massis.model.building;

import java.util.List;

import com.massisframework.massis.model.building.impl.SimDoorImpl.SimDoorState;

import straightedge.geom.PolygonHolder;

public interface ISimDoor extends PolygonHolder,RoomConnector{

	List<SimRoom> getConnectedRooms();

	String toString();

	boolean isOpened();

	boolean isClosed();

	void setOpen(boolean open);

	SimDoorState getState();

}
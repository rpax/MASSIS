package com.massisframework.massis.model.building;

import com.massisframework.massis.model.building.impl.SimDoorImpl.SimDoorState;

import straightedge.geom.PolygonHolder;

public interface SimDoor extends PolygonHolder,RoomConnector{

	String toString();

	boolean isOpened();

	boolean isClosed();

	void setOpen(boolean open);

	SimDoorState getState();

}
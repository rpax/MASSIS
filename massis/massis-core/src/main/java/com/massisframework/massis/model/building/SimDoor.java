package com.massisframework.massis.model.building;


import straightedge.geom.PolygonHolder;

public interface SimDoor extends PolygonHolder,RoomConnector{

	String toString();

	boolean isOpened();

	boolean isClosed();

	void setOpen(boolean open);


}
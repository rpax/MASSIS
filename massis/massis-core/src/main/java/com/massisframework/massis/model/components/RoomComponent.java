package com.massisframework.massis.model.components;

import java.util.Collection;
import java.util.List;

import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.building.RoomConnector;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

public interface RoomComponent {

	/**
	 *
	 * @return the rooms ordered by distance, BFS
	 */
	List<RoomComponent> getRoomsOrderedByDistance();
	/**
	 *
	 * @return the connectors of this room. Using that room connectors one agent
	 *         can move from one room to another
	 */
	List<RoomConnector> getConnectedRoomConnectors();

	KPoint getBoundaryPointClosestTo(KPoint p);

	/**
	 *
	 * @return the people in this room (Agents)
	 */
	Collection<LowLevelAgent> getPeopleIn();

	boolean contains(double x, double y);

	KPolygon getPolygon();

}
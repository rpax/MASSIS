package com.massisframework.massis.model.building;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.util.geom.CoordinateHolder;
import com.massisframework.massis.util.io.JsonState;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import straightedge.geom.KPoint;
import straightedge.geom.vision.Occluder;

public interface ISimRoom extends Occluder, Steppable, Stoppable,CoordinateHolder{

	/**
	 *
	 * @return the rooms ordered by distance, BFS
	 */
	List<ISimRoom> getRoomsOrderedByDistance();

	/**
	 *
	 * @return the connectors of this room. Using that room connectors one agent
	 *         can move from one room to another
	 */
	List<RoomConnector> getConnectedRoomConnectors();

	KPoint getBoundaryPointClosestTo(KPoint p);

	KPoint[] getBoundaryPointsClosestTo(KPoint p, int npoints);

	double getDistanceOfBoundaryPointClosestTo(KPoint p);

	/**
	 * @deprecated use {@link #getPeopleIn()} instead
	 * @return the people in this room.
	 *
	 */
	Iterator<DefaultAgent> getPeopleInIterator();

	/**
	 *
	 * @return the people in this room (Agents)
	 */
	Collection<DefaultAgent> getPeopleIn();

	void step(SimState s);

	void stop();

	JsonState<Building> getState();

	Location getRandomLoc();

}
/**
 *
 */
package com.massisframework.massis.model.building.impl;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.building.Building;
import com.massisframework.massis.model.building.RoomConnector;
import com.massisframework.massis.model.building.SimRoom;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.location.SimLocation;
import com.massisframework.massis.model.managers.AnimationManager;
import com.massisframework.massis.model.managers.EnvironmentManager;
import com.massisframework.massis.model.managers.pathfinding.PathFindingManager;
import com.massisframework.massis.util.geom.CoordinateHolder;
import com.massisframework.massis.util.geom.KPolygonUtils;
import com.massisframework.massis.util.io.JsonState;

import sim.engine.SimState;
import straightedge.geom.KPoint;

/**
 * Represents a room in MASSIS
 *
 * @author rpax
 *
 */
public class SimRoomImpl extends SimulationObjectImpl
		implements  SimRoom {

	private static final long serialVersionUID = 1L;
	/**
	 * The connectors attached to this room. (e.g Doors/teleports)
	 */
	private List<RoomConnector> connectedConnectors;
	/**
	 * Connected rooms , BFS order
	 */
	private List<SimRoom> roomsOrderedByDistance;
	// Cached values
	private final Collection<LowLevelAgent> vehiclesInThisRoomCached = new ArrayList<>();
	private boolean vehiclesInThisRoomComputed = false;

	public SimRoomImpl(Map<String, String> metadata, SimLocation location,
			AnimationManager animationManager,
			EnvironmentManager environment, PathFindingManager pathManager) {
		super(metadata, location, animationManager,
				environment, pathManager);
	}

	/**
	 * Searches for the connectors in this floor and adds them to the list
	 */
	private void computeDoorConnections() {
		this.connectedConnectors = new ArrayList<>();
		// Por sentido comun: las puertas que intersecta son los que conecta.
		for (RoomConnector sr : this.getRoomsConnectorsInSameFloor()) {
			if (this.getPolygon().intersects(sr.getPolygon())) {
				this.connectedConnectors.add(sr);
			} else {

				KPoint doorCenter = sr.getPolygon().getCenter();
				KPoint roomBound = this.getPolygon()
						.getBoundaryPointClosestTo(doorCenter);
				KPoint doorBound = sr.getPolygon()
						.getBoundaryPointClosestTo(roomBound);
				if (roomBound.distance(doorBound) < 1) {
					this.connectedConnectors.add(sr);
				}
			}

		}
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimRoom#getRoomsOrderedByDistance()
	 */
	@Override
	public List<SimRoom> getRoomsOrderedByDistance() {
		if (this.roomsOrderedByDistance == null) {
			this.roomsOrderedByDistance = new ArrayList<>();
			HashSet<SimRoom> visitedRooms = new HashSet<>();
			Queue<SimRoom> queue = new LinkedList<>();
			visitedRooms.add(this);
			queue.add(this);
			while (!queue.isEmpty()) {
				SimRoom currentRoom = queue.poll();
				visitedRooms.add(currentRoom);
				this.roomsOrderedByDistance.add(currentRoom);
				for (RoomConnector sd : currentRoom
						.getConnectedRoomConnectors()) {
					for (SimRoom sr : sd.getConnectedRooms()) {
						if (!visitedRooms.contains(sr)) {
							visitedRooms.add(sr);
							queue.add(sr);
						}
					}
				}
			}

		}
		return Collections.unmodifiableList(this.roomsOrderedByDistance);
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimRoom#getConnectedRoomConnectors()
	 */
	@Override
	public List<RoomConnector> getConnectedRoomConnectors() {

		if (this.connectedConnectors == null) {
			computeDoorConnections();
		}
		return Collections.unmodifiableList(connectedConnectors);
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimRoom#getBoundaryPointClosestTo(straightedge.geom.KPoint)
	 */
	@Override
	public KPoint getBoundaryPointClosestTo(KPoint p) {
		return this.getPolygon().getBoundaryPointClosestTo(p);
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimRoom#getBoundaryPointsClosestTo(straightedge.geom.KPoint, int)
	 */
	@Override
	public KPoint[] getBoundaryPointsClosestTo(KPoint p, int npoints) {
		return KPolygonUtils.getBoundaryPointsClosestTo(this.getPolygon(), p.x,
				p.y, npoints);
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimRoom#getDistanceOfBoundaryPointClosestTo(straightedge.geom.KPoint)
	 */
	@Override
	public double getDistanceOfBoundaryPointClosestTo(KPoint p) {

		return this.getPolygon().getBoundaryPointClosestTo(p).distance(p);

	}


	

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimRoom#step(sim.engine.SimState)
	 */
	@Override
	public void step(SimState s) {
		this.clearFlags();
	}

	private void clearFlags() {
		this.vehiclesInThisRoomComputed = false;
	}

	

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimRoom#stop()
	 */
	@Override
	public void stop() {
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimRoom#getState()
	 */
	@Override
	public JsonState<Building> getState() {
		throw new UnsupportedOperationException("Not supported yet");
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimRoom#getRandomLoc()
	 */
	@Override
	public CoordinateHolder getRandomLoc() {
		Random rnd = ThreadLocalRandom.current();
		Rectangle2D.Double bounds = this.getPolygon().getBounds2D();
		KPoint p = new KPoint();
		do {
			p.x = bounds.getX() + rnd.nextInt((int) bounds.getWidth());
			p.y = bounds.getY() + rnd.nextInt((int) bounds.getHeight());
			p = this.getLocation().getFloor()
					.getNearestPointOutsideOfObstacles(p);
		} while (!this.getPolygon().contains(p));
		return new Location(p.x, p.y, this.getLocation().getFloor());

	}
}

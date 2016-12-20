/**
 *
 */
package com.massisframework.massis.model.building;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.iterators.FilterIterator;

import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.components.RoomComponent;
import com.massisframework.massis.model.location.SimLocation;
import com.massisframework.massis.model.managers.AnimationManager;
import com.massisframework.massis.model.managers.EnvironmentManager;
import com.massisframework.massis.model.managers.movement.MovementManager;
import com.massisframework.massis.model.managers.pathfinding.PathFindingManager;
import com.massisframework.massis.util.io.JsonState;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import straightedge.geom.KPoint;
import straightedge.geom.vision.Occluder;

/**
 * Represents a room in MASSIS
 *
 * @author rpax
 *
 */
public class SimRoom extends SimulationObject
		implements Occluder, Steppable, Stoppable, RoomComponent {

	private static final long serialVersionUID = 1L;
	/**
	 * The connectors attached to this room. (e.g Doors/teleports)
	 */
	private List<RoomConnector> connectedConnectors;
	/**
	 * Connected rooms , BFS order
	 */
	private List<RoomComponent> roomsOrderedByDistance;
	// Cached values
	private final Collection<DefaultAgent> vehiclesInThisRoomCached = new ArrayList<>();
	private boolean vehiclesInThisRoomComputed = false;

	public SimRoom(Map<String, String> metadata, SimLocation location,
			MovementManager movementManager, AnimationManager animationManager,
			EnvironmentManager environment, PathFindingManager pathManager) {
		super(metadata, location, movementManager, animationManager,
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
	 * @see com.massisframework.massis.model.building.RoomComponent#getRoomsOrderedByDistance()
	 */
	@Override
	public List<RoomComponent> getRoomsOrderedByDistance() {
		if (this.roomsOrderedByDistance == null) {
			this.roomsOrderedByDistance = new ArrayList<>();
			HashSet<RoomComponent> visitedRooms = new HashSet<>();
			Queue<RoomComponent> queue = new LinkedList<>();
			visitedRooms.add(this);
			queue.add(this);
			while (!queue.isEmpty()) {
				RoomComponent currentRoom = queue.poll();
				visitedRooms.add(currentRoom);
				this.roomsOrderedByDistance.add(currentRoom);
				for (RoomConnector sd : currentRoom
						.getConnectedRoomConnectors()) {
					for (RoomComponent sr : sd.getConnectedRooms()) {
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
	 * @see com.massisframework.massis.model.building.RoomComponent#getConnectedRoomConnectors()
	 */
	@Override
	public List<RoomConnector> getConnectedRoomConnectors() {

		if (this.connectedConnectors == null) {
			computeDoorConnections();
		}
		return Collections.unmodifiableList(connectedConnectors);
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.RoomComponent#getBoundaryPointClosestTo(straightedge.geom.KPoint)
	 */
	@Override
	public KPoint getBoundaryPointClosestTo(KPoint p) {
		return this.getPolygon().getBoundaryPointClosestTo(p);
	}

	

	

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.RoomComponent#getPeopleInIterator()
	 */
	@Deprecated
	private Iterator<DefaultAgent> getPeopleInIterator() {
		return new FilterIterator<DefaultAgent>(
				this.getLocation().getFloor().getAgents().iterator(),
				new PeopleInThisRoomPredicate());
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.RoomComponent#getPeopleIn()
	 */
	@Override
	public Collection<DefaultAgent> getPeopleIn() {
		cacheVehiclesInThisRoom();
		return this.vehiclesInThisRoomCached;
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.RoomComponent#step(sim.engine.SimState)
	 */
	@Override
	public void step(SimState s) {
		this.clearFlags();
	}

	private void clearFlags() {
		this.vehiclesInThisRoomComputed = false;
	}

	/**
	 * TODO rpax why the quadtree is not being used here?? Caches the people in
	 * this room
	 */
	private void cacheVehiclesInThisRoom() {
		if (!this.vehiclesInThisRoomComputed) {
			this.vehiclesInThisRoomCached.clear();
			Iterator<DefaultAgent> it = this.getPeopleInIterator();
			while (it.hasNext()) {
				this.vehiclesInThisRoomCached.add(it.next());
			}
			this.vehiclesInThisRoomComputed = true;
		}
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.RoomComponent#stop()
	 */
	@Override
	public void stop() {
	}

	private class PeopleInThisRoomPredicate implements Predicate<DefaultAgent> {

		@Override
		public boolean evaluate(DefaultAgent person) {
			return (SimRoom.this == person.getRoom());
		}
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.RoomComponent#getState()
	 */
	@Override
	public JsonState<IBuilding> getState() {
		throw new UnsupportedOperationException("Not supported yet");
	}



	@Override
	public boolean contains(double x, double y)
	{
		return this.getPolygon().contains(x,y);
	}
}

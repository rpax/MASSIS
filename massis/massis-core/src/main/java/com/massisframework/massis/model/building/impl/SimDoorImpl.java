/**
 *
 */
package com.massisframework.massis.model.building.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.massisframework.massis.model.building.SimDoor;
import com.massisframework.massis.model.building.SimRoom;
import com.massisframework.massis.model.location.SimLocation;
import com.massisframework.massis.model.managers.AnimationManager;
import com.massisframework.massis.model.managers.EnvironmentManager;
import com.massisframework.massis.model.managers.pathfinding.PathFindingManager;

import straightedge.geom.KPoint;

/**
 * Represents a Door in MASSIS
 *
 * @author rpax
 *
 */
public class SimDoorImpl extends SimulationObjectImpl implements SimDoor {

	/**
	 * The rooms connected by this Door
	 */
	private List<SimRoom> connectedRooms;
	/**
	 * If it is open or not. by default is true.
	 */
	private boolean open = true;

	public SimDoorImpl(Map<String, String> metadata, SimLocation location,
			 AnimationManager animationManager,
			EnvironmentManager environment, PathFindingManager pathManager) {
		super(metadata, location, animationManager,
				environment, pathManager);
	}

	/**
	 * Iterates over all rooms in this floor and, if they intersect, are added
	 * to the connected rooms list.
	 */
	private void computeRoomConnections() {
		this.connectedRooms = new ArrayList<>();
		// Por sentido comun: los cuartos que intersecta son los que conecta.
		for (SimRoom sr : this.getLocation().getFloor().getRooms()) {
			if (this.getPolygon().intersects(sr.getPolygon())) {
				this.connectedRooms.add(sr);
			} else {

				KPoint doorCenter = sr.getPolygon().getCenter();
				KPoint roomBound = this.getPolygon()
						.getBoundaryPointClosestTo(doorCenter);
				KPoint doorBound = sr.getPolygon()
						.getBoundaryPointClosestTo(roomBound);
				if (roomBound.distance(doorBound) < 1) {
					this.connectedRooms.add(sr);
				}
			}

		}
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimDoor#getConnectedRooms()
	 */
	@Override
	public List<SimRoom> getConnectedRooms() {
		if (this.connectedRooms == null) {
			this.computeRoomConnections();
		}
		return Collections.unmodifiableList(connectedRooms);
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimDoor#toString()
	 */
	@Override
	public String toString() {

		return super.toString() + " [" + getConnectedRooms().get(0) + ","
				+ getConnectedRooms().get(1) + "]";
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimDoor#isOpened()
	 */
	@Override
	public boolean isOpened() {
		return this.open;
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimDoor#isClosed()
	 */
	@Override
	public boolean isClosed() {
		return !this.open;
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.ISimDoor#setOpen(boolean)
	 */
	@Override
	public void setOpen(boolean open) {
		this.open = open;
		this.notifyChanged();
	}

}

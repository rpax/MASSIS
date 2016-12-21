package com.massisframework.massis.model.components;

import java.util.List;

import com.massisframework.massis.sim.SimulationEntity;

public interface RoomComponent extends SimulationComponent {
	/**
	 * 
	 * @return the rooms ordered by distance to the center of this room
	 */
	public List<SimulationEntity> getRoomsOrderedByDistance();
	
	public List<SimulationEntity> getConnectedRoomConnectors();

}
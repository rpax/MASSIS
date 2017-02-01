package com.massisframework.massis.model.systems.sh3d;

import com.eteks.sweethome3d.model.Room;
import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;

public class SweetHome3DRoom implements SimulationComponent {

	private Room room;

	public Room getRoom()
	{
		return room;
	}

	public void setRoom(Room room)
	{
		this.room = room;
	}
}

package com.massisframework.massis.model.systems.sh3d;

import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.massisframework.massis.sim.ecs.SimulationComponent;

public class SweetHome3DFurniture implements SimulationComponent {

	private HomePieceOfFurniture furniture;

	public HomePieceOfFurniture getFurniture()
	{
		return furniture;
	}

	public void setFurniture(HomePieceOfFurniture furniture)
	{
		this.furniture = furniture;
	}

}

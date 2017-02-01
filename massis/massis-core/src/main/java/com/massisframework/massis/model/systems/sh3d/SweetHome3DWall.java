package com.massisframework.massis.model.systems.sh3d;

import com.eteks.sweethome3d.model.Wall;
import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;

public class SweetHome3DWall implements SimulationComponent {

	private Wall wall;

	public Wall getWall()
	{
		return wall;
	}

	public void setWall(Wall wall)
	{
		this.wall = wall;
	}
}

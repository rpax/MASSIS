package com.massisframework.massis.model.systems.sh3d;

import com.eteks.sweethome3d.model.Level;
import com.massisframework.massis.sim.ecs.SimulationComponent;

public class SweetHome3DLevel implements SimulationComponent {

	private Level level;

	public Level getLevel()
	{
		return level;
	}

	public void setLevel(Level level)
	{
		this.level = level;
	}
}

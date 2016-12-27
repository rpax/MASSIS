package com.massisframework.massis.ecs.system.sweethome3d;

import com.artemis.Component;
import com.eteks.sweethome3d.model.Level;

public class SweetHome3DLevelComponent extends Component {

	private Level level;

	public SweetHome3DLevelComponent()
	{
	}

	public Level getLevel()
	{
		return level;
	}

	public void set(Level level)
	{
		this.level = level;
	}
}

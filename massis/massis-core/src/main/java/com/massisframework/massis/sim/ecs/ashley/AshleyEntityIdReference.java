package com.massisframework.massis.sim.ecs.ashley;

import com.badlogic.ashley.core.Component;

public class AshleyEntityIdReference implements Component {
	public int ashleyId;

	public AshleyEntityIdReference(int ashleyId)
	{
		this.ashleyId = ashleyId;
	}
}

package com.massisframework.massis.sim.engine.impl;

import com.badlogic.ashley.core.Entity;
import com.massisframework.massis.sim.SimulationEntity;

public interface AshleyEntityWrapper extends SimulationEntity{

	public Entity getEntity();
	public void setEntity(Entity e);
}

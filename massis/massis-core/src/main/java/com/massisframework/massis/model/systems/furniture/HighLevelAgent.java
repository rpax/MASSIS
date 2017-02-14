package com.massisframework.massis.model.systems.furniture;

import com.massisframework.massis.sim.ecs.SimulationEntity;

public interface HighLevelAgent {

	public void update(float tpf, SimulationEntity entity);
}

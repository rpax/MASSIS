package com.massisframework.massis.sim.ecs.zayes;

import com.massisframework.massis.sim.ecs.SimulationSystem;

public interface SimulationSystemCreator {

	SimulationSystem createSystem(Class<? extends SimulationSystem> stateType);

}

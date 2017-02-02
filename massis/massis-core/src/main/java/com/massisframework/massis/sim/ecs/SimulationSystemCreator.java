package com.massisframework.massis.sim.ecs;

public interface SimulationSystemCreator {

	SimulationSystem createSystem(Class<? extends SimulationSystem> stateType);

}

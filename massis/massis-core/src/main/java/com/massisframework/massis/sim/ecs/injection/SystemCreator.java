package com.massisframework.massis.sim.ecs.injection;

import com.massisframework.massis.sim.ecs.SimulationSystem;

public interface SystemCreator {

	<T extends SimulationSystem> T createSystem(Class<T> type);

}
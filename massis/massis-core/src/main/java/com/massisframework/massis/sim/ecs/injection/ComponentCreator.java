package com.massisframework.massis.sim.ecs.injection;

import com.massisframework.massis.sim.ecs.SimulationComponent;;

public interface ComponentCreator {

	<T extends SimulationComponent> T createComponent(Class<T> type);

}
package com.massisframework.massis.sim.ecs.injection.components;

import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;;

public interface ComponentCreator<E extends SimulationEntity<E>> {

	public <T extends SimulationComponent> T createComponent(
			E e,
			Class<T> type);
}
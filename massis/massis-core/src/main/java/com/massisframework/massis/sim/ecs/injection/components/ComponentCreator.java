package com.massisframework.massis.sim.ecs.injection.components;

import com.massisframework.massis.sim.ecs.OLDSimulationEntity;
import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;;

public interface ComponentCreator<E extends OLDSimulationEntity<E>> {

	public <T extends SimulationComponent> T createComponent(
			E e,
			Class<T> type);
}
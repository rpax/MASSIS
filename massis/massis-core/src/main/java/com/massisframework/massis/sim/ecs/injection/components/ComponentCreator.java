package com.massisframework.massis.sim.ecs.injection.components;

import java.util.List;

import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;;

public interface ComponentCreator {

	public <T extends SimulationComponent> T createComponent(
			SimulationEntity e,
			Class<T> type);
}
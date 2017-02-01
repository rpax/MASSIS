package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;

public interface NameComponent extends SimulationComponent {

	String get();

	void set(String v);

}

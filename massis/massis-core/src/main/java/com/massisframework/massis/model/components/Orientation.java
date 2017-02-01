package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;

public interface Orientation extends SimulationComponent {

	double getAngle();

	Orientation setAngle(float angle);

}

package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;
import com.massisframework.massis.util.geom.KVector;

public interface Velocity extends SimulationComponent{

	KVector getValue();

	void setValue(KVector velocity);

	

}

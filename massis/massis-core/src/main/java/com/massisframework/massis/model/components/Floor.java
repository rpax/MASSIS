package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.OLDSimulationEntity;
import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;

public interface Floor extends SimulationComponent {

	public int getYlength();

	public int getXlength();

	public int getMinY();

	public int getMinX();

	public int getMaxY();

	public int getMaxX();

	Iterable<OLDSimulationEntity<?>> getEntitiesIn();

}
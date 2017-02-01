package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntity;

public interface Floor extends SimulationComponent {

	public Iterable<SimulationEntity> getEntitiesIn();
	
	public int getYlength();

	public int getXlength();

	public int getMinY();

	public int getMinX();

	public int getMaxY();

	public int getMaxX();

}
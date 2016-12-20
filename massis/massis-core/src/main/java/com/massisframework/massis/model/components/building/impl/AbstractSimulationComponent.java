package com.massisframework.massis.model.components.building.impl;

import com.massisframework.massis.model.components.SimulationComponent;
import com.massisframework.massis.model.components.SimulationEntity;

public abstract class AbstractSimulationComponent implements SimulationComponent {

	private SimulationEntity entity;

	public void setEntity(SimulationEntity se)
	{
		this.entity = se;
	}

	public SimulationEntity getEntity()
	{
		return this.entity;
	}

}

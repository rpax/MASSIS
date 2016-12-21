package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.SimulationEntity;

public interface SimulationComponent {

	public default void setEntity(SimulationEntity se)
	{
	}

	public default void step(float tpf)
	{
	}
}

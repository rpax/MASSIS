package com.massisframework.massis.model.components;

public interface SimulationComponent {

	public default void setEntity(SimulationEntity se)
	{
	}

	public default void step(float tpf)
	{
	}
}

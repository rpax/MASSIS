package com.massisframework.massis.model.components;

import com.badlogic.ashley.core.Component;

public interface SimulationComponent extends Component{

	
	public default void step(float tpf)
	{
	}
}

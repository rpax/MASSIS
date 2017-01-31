package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.SimulationComponent;

public interface RenderComponent extends SimulationComponent{

	public void setRenderer(JFXRenderer r);
}

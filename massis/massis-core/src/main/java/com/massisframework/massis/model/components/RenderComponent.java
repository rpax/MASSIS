package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;

public interface RenderComponent extends SimulationComponent {

	public void setRenderer(JFXRenderer r);

	JFXRenderer getRenderer();
}

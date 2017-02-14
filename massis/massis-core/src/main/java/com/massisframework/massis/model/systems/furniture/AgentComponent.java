package com.massisframework.massis.model.systems.furniture;

import com.massisframework.massis.sim.ecs.SimulationComponent;

public interface AgentComponent extends SimulationComponent {

	HighLevelAgent getHighLevelAgent();

}
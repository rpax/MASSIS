package com.massisframework.massis.model.systems.furniture;

import com.massisframework.massis.sim.ecs.SimulationComponent;

public class AgentComponentImpl implements SimulationComponent, AgentComponent {

	private HighLevelAgent highLevelAgent;

	@Override
	public HighLevelAgent getHighLevelAgent()
	{
		return highLevelAgent;
	}

	public void setHighLevelAgent(HighLevelAgent highLevelAgent)
	{
		this.highLevelAgent = highLevelAgent;
	}

}

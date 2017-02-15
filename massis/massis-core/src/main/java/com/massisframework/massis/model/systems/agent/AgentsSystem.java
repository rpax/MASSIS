package com.massisframework.massis.model.systems.agent;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.impl.VelocityImpl;
import com.massisframework.massis.model.components.impl.VisionAreaImpl;
import com.massisframework.massis.model.systems.furniture.AgentComponent;
import com.massisframework.massis.model.systems.furniture.AgentComponentImpl;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationEntityData;
import com.massisframework.massis.sim.ecs.SimulationEntitySet;
import com.massisframework.massis.sim.ecs.SimulationSystem;

public class AgentsSystem implements SimulationSystem {

	private SimulationEntitySet agents;
	@Inject
	private SimulationEntityData ed;

	@Override
	public void initialize()
	{
		this.agents = this.ed.createEntitySet(AgentComponent.class);
		this.agents.applyChanges();
		this.agents.forEach(this::initAgent);
	}

	private void initAgent(SimulationEntity se)
	{
		se.add(new VisionAreaImpl());
		se.add(new VelocityImpl());
		// y el HL?

	}

	@Override
	public void update(float deltaTime)
	{
		if (this.agents.applyChanges())
		{
			this.agents.getAddedEntities().forEach(this::initAgent);
		}
		for (SimulationEntity e : agents)
		{
			e.get(AgentComponent.class).getHighLevelAgent().update(deltaTime,e);
		}
		//System.out.println(this.ed.findEntities(AgentComponentImpl.class));

	}

}

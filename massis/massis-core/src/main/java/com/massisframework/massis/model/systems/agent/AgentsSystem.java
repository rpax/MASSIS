package com.massisframework.massis.model.systems.agent;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.Metadata;
import com.massisframework.massis.model.components.impl.DynamicObstacleImpl;
import com.massisframework.massis.model.components.impl.VelocityImpl;
import com.massisframework.massis.model.components.impl.VisionAreaImpl;
import com.massisframework.massis.model.systems.furniture.AgentComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationEntityData;
import com.massisframework.massis.sim.ecs.SimulationEntitySet;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.util.SimObjectProperty;

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
		String dyn = se.get(Metadata.class).get(SimObjectProperty.IS_DYNAMIC);
		if (!"false".equalsIgnoreCase(dyn))
		{
			se.add(new VelocityImpl());
			se.add(new DynamicObstacleImpl());
		}
		se.add(new VisionAreaImpl());
		// y el HL?

	}

	@Override
	public void update(float deltaTime)
	{
		// TODO Auto-generated method stub

	}

}

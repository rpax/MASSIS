package com.massisframework.massis.sim.engine.impl;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.massisframework.massis.sim.engine.SimulationEngine;
import com.massisframework.massis.sim.engine.SimulationSystem;

public class EntitySystemWrapper extends EntitySystem {

	private SimulationSystem simulationSystem;
	private SimulationEngine simEngine;
	
	
	public EntitySystemWrapper(
			SimulationEngine simEngine,
			SimulationSystem simulationSystem)
	{
		this.simEngine = simEngine;
		this.simulationSystem = simulationSystem;
	}

	@Override
	public void addedToEngine(Engine engine)
	{
		super.addedToEngine(engine);
		this.simulationSystem.addedToEngine(this.simEngine);
	}

	@Override
	public void removedFromEngine(Engine engine)
	{
		super.removedFromEngine(engine);
		this.simulationSystem.removedFromEngine(this.simEngine);
	}

	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
		this.simulationSystem.update(deltaTime);
	}

	@Override
	public boolean checkProcessing()
	{
		return super.checkProcessing();
	}

	@Override
	public void setProcessing(boolean processing)
	{
		super.setProcessing(processing);
	}

	@Override
	public Engine getEngine()
	{
		return super.getEngine();
	}

}

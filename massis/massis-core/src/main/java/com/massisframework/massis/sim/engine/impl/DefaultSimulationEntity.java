package com.massisframework.massis.sim.engine.impl;

import com.badlogic.ashley.core.Entity;
import com.massisframework.massis.model.components.SimulationComponent;
import com.massisframework.massis.sim.engine.SimulationEngine;

public class DefaultSimulationEntity
		implements  AshleyEntityWrapper {

	protected Entity entity;
	//TODO weak reference?
	protected SimulationEngine engine;
	

	@Override
	public <T extends SimulationComponent> T get(Class<T> type)
	{
		return this.engine.getComponent(this,type);
	}

	@Override
	public <T extends SimulationComponent> boolean has(Class<T> type)
	{
		return this.get(type) != null;
	}

	@Override
	public <T extends SimulationComponent> void set(T component)
	{
		this.entity.add(component);
	}

	@Override
	public <T extends SimulationComponent> void remove(Class<T> type)
	{
		this.entity.remove(type);
	}

	@Override
	public Entity getEntity()
	{
		return this.entity;
	}

	public void setEntity(Entity entity)
	{
		this.entity = entity;
	}

	public SimulationEngine getEngine()
	{
		return engine;
	}

	public void setEngine(SimulationEngine engine)
	{
		this.engine = engine;
	}

}

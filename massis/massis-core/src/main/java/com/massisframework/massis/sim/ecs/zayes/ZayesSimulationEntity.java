package com.massisframework.massis.sim.ecs.zayes;

import com.google.inject.Inject;
import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.OLDSimulationEntity;

public class ZayesSimulationEntity
		implements OLDSimulationEntity<ZayesSimulationEntity> {

	@Inject
	public ZayesSimulationEntity()
	{
		
	}
	
	@Override
	public int getId()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T extends SimulationComponent> T addComponent(Class<T> type)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends SimulationComponent> void remove(Class<T> type)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends SimulationComponent> T get(Class<T> type)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<SimulationComponent> getComponents()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<ZayesSimulationEntity> getChildren()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addChild(ZayesSimulationEntity e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeChild(ZayesSimulationEntity e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public ZayesSimulationEntity getParent()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendMessage(Object msg)
	{
		// TODO Auto-generated method stub

	}

}

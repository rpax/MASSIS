package com.massisframework.massis.sim.ecs.zayes;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.massisframework.massis.sim.ecs.ComponentFactory;
import com.massisframework.massis.sim.ecs.SimulationComponent;

public class ComponentFactoryImpl implements ComponentFactory {

	private Injector injector;

	@Inject
	public ComponentFactoryImpl(Injector injector)
	{
		this.injector = injector;
	}

	@Override
	public <T extends SimulationComponent> T create(Class<T> type)
	{
		T cmp = injector.getInstance(type);
		return cmp;
	}

}

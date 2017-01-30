package com.massisframework.massis.sim.ecs.injection.components;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.massisframework.massis.sim.ecs.injection.SimulationConfiguration;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ComponentsModule extends AbstractModule {

	private SimulationConfiguration config;

	public ComponentsModule(SimulationConfiguration config)
	{
		this.config = config;
	}

	@Override
	protected void configure()
	{

		this.config.getBindings().forEach((k, v) -> bind(k).to((Class) v));
		bind(ComponentCreator.class)
				.to(ComponentCreatorImpl.class)
				.in(Singleton.class);
		bind(SimulationConfiguration.class).toInstance(config);
	}

}

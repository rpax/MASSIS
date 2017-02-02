package com.massisframework.massis.sim.ecs.mason;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.massisframework.massis.sim.SimulationScheduler;

public class MasonModule extends AbstractModule {

	@Override
	protected void configure()
	{
		bind(SimulationScheduler.class)
				.to(MasonScheduler.class)
				.in(Singleton.class);
	}

}

package com.massisframework.massis.sim;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.injection.ConfigurationModule;
import com.massisframework.massis.sim.ecs.injection.SimulationConfiguration;

public class SimulationLauncher {

	private SimulationLauncher()
	{
		// fill default values:

	}

	public static void launch(SimulationConfiguration config)
	{
		Injector injector = Guice.createInjector(
				new ConfigurationModule(config));
		SimulationEngine<?> engine = injector
				.getInstance(SimulationEngine.class);
		// add default systems:
		config.getSystems().forEach(engine::addSystem);

		engine.start();
	}

}

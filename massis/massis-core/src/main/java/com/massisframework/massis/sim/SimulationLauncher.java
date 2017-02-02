package com.massisframework.massis.sim;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.injection.EventBusModule;
import com.massisframework.massis.sim.ecs.mason.MasonModule;
import com.massisframework.massis.sim.ecs.zayes.InterfaceBindings;
import com.massisframework.massis.sim.ecs.zayes.SystemsManager;
import com.massisframework.massis.sim.ecs.zayes.ZayEsModule;

public class SimulationLauncher {

	private SimulationLauncher()
	{
		// fill default values:

	}
	
	public static void launch(InterfaceBindings bindings)
	{
		Injector injector = Guice.createInjector(new ZayEsModule(bindings),
				new EventBusModule(),new MasonModule());
		SimulationScheduler ss = injector
				.getInstance(SimulationScheduler.class);
		bindings.getSystems();
		SystemsManager sm = injector.getInstance(SystemsManager.class);
		
		sm.addAll(bindings.getSystems());
		
		ss.start();
		ss.scheduleRepeating(sm);
	}

}

package com.massisframework.massis.sim.engine.base;

import com.eteks.sweethome3d.model.Home;
import com.massisframework.massis.model.managers.SweetHome3DManagerSystem;
import com.massisframework.massis.sim.engine.SimulationEngine;
import com.massisframework.massis.sim.engine.impl.SimulationEngineImpl;
import com.massisframework.massis.sim.systems.Display2DSystem;

public class Simulations {

	public static SimulationEngine createSimulationEngine(Home home)
	{
		SimulationEngine engine = new SimulationEngineImpl();
		engine.registerSystem(SweetHome3DManagerSystem.class);
		engine.registerSystem(Display2DSystem.class);
		return engine;
	}
}

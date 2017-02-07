package com.massisframework.massis.sim.scheduler.mason;

import com.massisframework.massis.sim.SimulationSteppable;

import sim.engine.SimState;
import sim.engine.Steppable;

@SuppressWarnings("serial")
class SteppableWrapper implements Steppable {

	// TODO ?
	private SimulationSteppable simulationSteppable;
	private int fps = 60;

	public SteppableWrapper(SimulationSteppable simulationSteppable)
	{
		this.simulationSteppable = simulationSteppable;
	}

	@Override
	public void step(SimState simState)
	{
		double stepSize = 1D / fps;
//		for (int i = 0; i < 10; i++)
		{
			simulationSteppable.update((float) stepSize);
		}
	}
}

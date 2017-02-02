package com.massisframework.massis.sim.ecs.mason;

import com.massisframework.massis.sim.SimulationSteppable;

import sim.engine.SimState;
import sim.engine.Steppable;

@SuppressWarnings("serial")
public class SteppableWrapper implements Steppable {

	// TODO ?
	private float lastUpdate = -1;
	private SimulationSteppable simulationSteppable;

	public SteppableWrapper(SimulationSteppable simulationSteppable)
	{
		this.simulationSteppable = simulationSteppable;
	}

	@Override
	public void step(SimState simState)
	{
		float currentTime = (float) (simState.schedule.getTime() / 60);
		if (lastUpdate > 0)
		{
			simulationSteppable.update(currentTime - lastUpdate);
		}
		lastUpdate = currentTime;
	}

}

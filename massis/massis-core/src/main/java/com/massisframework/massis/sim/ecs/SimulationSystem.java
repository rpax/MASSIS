package com.massisframework.massis.sim.ecs;

import com.massisframework.massis.sim.SimulationSteppable;

public interface SimulationSystem
		extends SimulationSteppable, SimulationInitializable {

	public default void onAdded()
	{
	}

	public default void onRemoved()
	{
	}

	public default void onEntityAdded(long entityId)
	{
	}

	public default void onEntityRemoved(long entityId)
	{
	}

	public default void cleanup()
	{
	}

	

}

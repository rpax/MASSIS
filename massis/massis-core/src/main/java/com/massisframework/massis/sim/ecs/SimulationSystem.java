package com.massisframework.massis.sim.ecs;

public interface SimulationSystem {

	public default void onAdded()
	{
	}

	public void initialize();

	public void update(float deltaTime);

	public default void onRemoved()
	{
	}

	public default void onEntityAdded(long entityId)
	{
	}

	public default void onEntityRemoved(long entityId)
	{
	}

	public default void cleanup(){}

	public default boolean isEnabled(){return true;}

}

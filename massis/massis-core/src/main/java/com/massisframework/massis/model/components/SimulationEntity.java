package com.massisframework.massis.model.components;

public interface SimulationEntity {

	public long getId();

	public <T extends SimulationComponent> T get(Class<T> type);

	public <T extends SimulationComponent> boolean has(Class<T> type);

	public <T extends SimulationComponent> void set(T component);

	public <T extends SimulationComponent> void remove(Class<T> type);

	public default void componentChanged(SimulationComponent cmp)
	{
	}

}

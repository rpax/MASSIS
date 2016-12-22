package com.massisframework.massis.sim;

import com.massisframework.massis.model.components.SimulationComponent;

public interface SimulationEntity {


	public <T extends SimulationComponent> T get(Class<T> type);

	public <T extends SimulationComponent> boolean has(Class<T> type);

	public <T extends SimulationComponent> void set(T component);

	public <T extends SimulationComponent> void remove(Class<T> type);


}

package com.massisframework.massis.sim.ecs;

public interface ComponentFactory {

	public <T extends SimulationComponent> T create(Class<T> type);
}

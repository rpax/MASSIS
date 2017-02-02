
package com.massisframework.massis.sim.ecs;

public interface EntityComponentCreator {

	public <T extends SimulationComponent> T create(Class<T> type);
}

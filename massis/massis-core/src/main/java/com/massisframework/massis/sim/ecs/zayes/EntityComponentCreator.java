
package com.massisframework.massis.sim.ecs.zayes;

public interface EntityComponentCreator {

	public <T extends SimulationComponent> T create(Class<T> type);
}

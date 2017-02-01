
package com.massisframework.massis.sim.ecs.zayes;

public interface EntityComponentCreator {

	public <T extends SimulationEntityComponent> T create(Class<T> type);
}

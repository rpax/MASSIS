package com.massisframework.massis.sim.ecs.zayes;

import com.simsilica.es.EntityId;

public interface SimulationEntity {

	public SimulationEntityComponent[] getComponents();

	public <T extends SimulationEntityComponent> EntityEdit<T> addC(Class<T> c);

	public <T extends SimulationEntityComponent> T getC(Class<T> c);

	public <T extends SimulationEntityComponent> void removeC(Class<T> c);

	public <T extends SimulationEntityComponent> EntityEdit<T> editC(
			Class<T> c);

	public EntityId getId();

}

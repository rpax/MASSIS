package com.massisframework.massis.sim.ecs.zayes;

import com.simsilica.es.EntityId;

public interface SimulationEntity {

	public SimulationComponent[] getComponents();

	public <T extends SimulationComponent> EntityEdit<T> addC(Class<T> c);

	public <T extends SimulationComponent> T getC(Class<T> c);

	public <T extends SimulationComponent> void removeC(Class<T> c);

	public <T extends SimulationComponent> EntityEdit<T> editC(
			Class<T> c);

	public SimulationEntity getParent();

	public Iterable<SimulationEntity> getChildren();

	public EntityId getId();

	void setParent(SimulationEntity se);

	void addChild(EntityId child);

	void setParent(EntityId parent);

	void removeFromParent();

}

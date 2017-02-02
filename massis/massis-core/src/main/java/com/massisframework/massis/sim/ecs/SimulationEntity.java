package com.massisframework.massis.sim.ecs;

import com.simsilica.es.EntityId;

public interface SimulationEntity extends ComponentModifier {

	public <T extends SimulationComponent> T getComponent(Class<T> c);

	public SimulationEntity getParent();

	public Iterable<SimulationEntity> getChildren();

	public EntityId getId();

	void setParent(SimulationEntity se);

	void addChild(EntityId child);

	void setParent(EntityId parent);

	void removeFromParent();

	public SimulationComponent[] getAllComponents();

}

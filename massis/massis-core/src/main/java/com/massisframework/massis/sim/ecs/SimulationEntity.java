package com.massisframework.massis.sim.ecs;

public interface SimulationEntity extends ComponentModifier {

	public <T extends SimulationComponent> T get(Class<T> c);

	public SimulationEntity getParent();

	public Iterable<SimulationEntity> getChildren();

	public long id();

	void setParent(SimulationEntity se);

	void addChild(long child);

	void setParent(Long parent);

	void removeFromParent();

	//public SimulationComponent[] getAllComponents();

}

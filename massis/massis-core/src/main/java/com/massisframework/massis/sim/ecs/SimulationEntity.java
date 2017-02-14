package com.massisframework.massis.sim.ecs;

public interface SimulationEntity {

	public <T extends SimulationComponent> T get(Class<T> type);

	public <T extends SimulationComponent> T add(T cmp);

	public <T extends SimulationComponent> void remove(Class<T> type);

	public <T extends SimulationComponent> void markChanged(Class<T> type);

	public SimulationEntity getParent();

	public Iterable<SimulationEntity> getChildren();

	public long id();

	void setParent(SimulationEntity se);

	void addChild(long child);

	void setParent(Long parent);

	void removeFromParent();

	// public SimulationComponent[] getAllComponents();

}

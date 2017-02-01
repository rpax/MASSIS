package com.massisframework.massis.sim.ecs;

public interface OLDSimulationEntity<E extends OLDSimulationEntity<E>> {

	public int getId();

	public <T extends SimulationComponent> T addComponent(Class<T> type);

	public <T extends SimulationComponent> void remove(Class<T> type);

	public <T extends SimulationComponent> T get(Class<T> type);

	public Iterable<SimulationComponent> getComponents();

	public Iterable<E> getChildren();

	public void addChild(E e);

	public void removeChild(E e);

	public E getParent();

	public void sendMessage(Object msg);

}

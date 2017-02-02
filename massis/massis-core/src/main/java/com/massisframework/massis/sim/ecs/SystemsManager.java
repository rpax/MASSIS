package com.massisframework.massis.sim.ecs;

import java.util.Collection;

import com.massisframework.massis.sim.SimulationSteppable;

public interface SystemsManager extends SimulationSteppable {

	<T extends SimulationSystem> T getSystem(Class<T> type);

	public Collection<SimulationSystem> getActiveSystems();

	/**
	 * Check if a state is attached or not.
	 *
	 * @param state
	 *            The state to check
	 * @return True if the state is currently attached to this AppStateManager.
	 * 
	 * @see SystemsManagerImpl#attach(com.jme3.app.state.AppState)
	 */
	boolean hasSystem(Class<? extends SimulationSystem> stateType);

	void add(Class<? extends SimulationSystem> systems);

	void addAll(Iterable<Class<? extends SimulationSystem>> systems);

	public void setEnabled(Class<? extends SimulationSystem> stateType,
			boolean value);
}
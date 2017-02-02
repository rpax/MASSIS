package com.massisframework.massis.sim.ecs.zayes;

import com.massisframework.massis.sim.SimulationSteppable;
import com.massisframework.massis.sim.ecs.SimulationSystem;

public interface SystemsManager extends SimulationSteppable {

	<T extends SimulationSystem> T getState(Class<T> type);

	/**
	 * Check if a state is attached or not.
	 *
	 * @param state
	 *            The state to check
	 * @return True if the state is currently attached to this AppStateManager.
	 * 
	 * @see SystemsManagerImpl#attach(com.jme3.app.state.AppState)
	 */
	boolean hasState(Class<? extends SimulationSystem> stateType);

	void addAll(Iterable<Class<? extends SimulationSystem>> systems);

}
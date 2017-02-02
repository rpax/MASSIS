package com.massisframework.massis.sim.ecs.zayes;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;
import com.jme3.util.SafeArrayList;
import com.massisframework.massis.sim.SimulationScheduler;
import com.massisframework.massis.sim.SimulationSteppable;
import com.massisframework.massis.sim.ecs.SimulationSystem;

public class SystemsManagerImpl implements SystemsManager {

	/**
	 * List holding the attached app states that are pending initialization.
	 * Once initialized they will be added to the running app states.
	 */
	private final SafeArrayList<SimulationSystem> initializing = new SafeArrayList<>(
			SimulationSystem.class);

	/**
	 * Holds the active states once they are initialized.
	 */
	private final SafeArrayList<SimulationSystem> states = new SafeArrayList<>(
			SimulationSystem.class);

	/**
	 * List holding the detached app states that are pending cleanup.
	 */
	private final SafeArrayList<SimulationSystem> terminating = new SafeArrayList<>(
			SimulationSystem.class);

	// All of the above lists need to be thread safe but access will be
	// synchronized separately.... but always on the states list. This
	// is to avoid deadlocking that may occur and the most common use case
	// is that they are all modified from the same thread anyway.

	private SimulationSystem[] stateArray;

	private SimulationSystemCreator systemsCreator;

	@Inject
	public SystemsManagerImpl(SimulationSystemCreator systemsCreator)
	{
		this.systemsCreator = systemsCreator;
	}

	protected SimulationSystem[] getInitializing()
	{
		synchronized (states)
		{
			return initializing.getArray();
		}
	}

	protected SimulationSystem[] getTerminating()
	{
		synchronized (states)
		{
			return terminating.getArray();
		}
	}

	protected SimulationSystem[] getStates()
	{
		synchronized (states)
		{
			return states.getArray();
		}
	}

	/**
	 * Attach a state to the AppStateManager, the same state cannot be attached
	 * twice.
	 *
	 * @param state
	 *            The state to attach
	 * @return True if the state was successfully attached, false if the state
	 *         was already attached.
	 */
	public boolean attach(Class<? extends SimulationSystem> stateType)
	{
		synchronized (states)
		{

			if (!hasState(stateType))
			{
				SimulationSystem state = systemsCreator.createSystem(stateType);
				state.initialize();
				initializing.add(state);
				return true;
			} else
			{
				return false;
			}
		}
	}

	/**
	 * Attaches many state to the AppStateManager in a way that is guaranteed
	 * that they will all get initialized before any of their updates are run.
	 * The same state cannot be attached twice and will be ignored.
	 *
	 * @param states
	 *            The states to attach
	 */
	public void attachAll(Class<? extends SimulationSystem>... states)
	{
		attachAll(Arrays.asList(states));
	}

	/**
	 * Attaches many state to the AppStateManager in a way that is guaranteed
	 * that they will all get initialized before any of their updates are run.
	 * The same state cannot be attached twice and will be ignored.
	 *
	 * @param states
	 *            The states to attach
	 */
	public void attachAll(Iterable<Class<? extends SimulationSystem>> states)
	{
		synchronized (this.states)
		{
			for (Class<? extends SimulationSystem> state : states)
			{
				attach(state);
			}
		}
	}

	/**
	 * Detaches the state from the AppStateManager.
	 *
	 * @param state
	 *            The state to detach
	 * @return True if the state was detached successfully, false if the state
	 *         was not attached in the first place.
	 */
	public boolean detach(Class<? extends SimulationSystem> stateType)
	{
		synchronized (states)
		{
			if (contains(stateType, this.states))
			{
				SimulationSystem state = getStateIn(stateType, this.states);
				state.onRemoved();
				states.remove(state);
				terminating.add(state);
				return true;
			} else if (contains(stateType, this.initializing))
			{
				SimulationSystem state = getStateIn(stateType,
						this.initializing);
				state.onRemoved();
				initializing.remove(state);
				return true;
			} else
			{
				return false;
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.sim.ecs.zayes.SystemsManager#getState(java.lang.Class)
	 */
	@Override
	public <T extends SimulationSystem> T getState(Class<T> type)
	{
		SimulationSystem s = getStateIn(type, this.initializing);
		if (s == null)
		{
			s = getStateIn(type, this.states);
		}
		return (T) s;
	}

	private static SimulationSystem getStateIn(
			Class<? extends SimulationSystem> stateType,
			List<? extends SimulationSystem> list)
	{
		return list.stream()
				.filter(c -> stateType.isAssignableFrom(c.getClass())).findAny()
				.orElseGet(null);
	}

	private static boolean contains(Class<? extends SimulationSystem> stateType,
			List<? extends SimulationSystem> list)
	{
		return list.stream()
				.map(s -> s.getClass())
				.filter(c -> c == stateType)
				.findAny()
				.isPresent();
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.sim.ecs.zayes.SystemsManager#hasState(java.lang.Class)
	 */
	@Override
	public boolean hasState(Class<? extends SimulationSystem> stateType)
	{
		synchronized (states)
		{
			return     contains(stateType, initializing)
					|| contains(stateType, states);
		}
	}

	protected void initializePending()
	{
		SimulationSystem[] array = getInitializing();
		if (array.length == 0)
			return;

		synchronized (states)
		{
			// Move the states that will be initialized
			// into the active array. In all but one case the
			// order doesn't matter but if we do this here then
			// a state can detach itself in initialize(). If we
			// did it after then it couldn't.
			List<SimulationSystem> transfer = Arrays.asList(array);
			states.addAll(transfer);
			initializing.removeAll(transfer);
		}
		for (SimulationSystem state : array)
		{
			state.initialize();
		}
	}

	protected void terminatePending()
	{
		SimulationSystem[] array = getTerminating();
		if (array.length == 0)
			return;

		for (SimulationSystem state : array)
		{
			state.cleanup();
		}
		synchronized (states)
		{
			// Remove just the states that were terminated...
			// which might now be a subset of the total terminating
			// list.
			terminating.removeAll(Arrays.asList(array));
		}
	}

	/**
	 * Calls update for attached states, do not call directly.
	 * 
	 * @param tpf
	 *            Time per frame.
	 */
	public void update(float tpf)
	{

		// Cleanup any states pending
		terminatePending();

		// Initialize any states pending
		initializePending();

		// Update enabled states
		SimulationSystem[] array = getStates();
		for (SimulationSystem state : array)
		{
			if (state.isEnabled())
			{
				state.update(tpf);
			}
		}
	}

	// /**
	// * Calls render for all attached and initialized states, do not call
	// * directly.
	// *
	// * @param rm
	// * The RenderManager
	// */
	// public void render(RenderManager rm)
	// {
	// AppState[] array = getStates();
	// for (AppState state : array)
	// {
	// if (state.isEnabled())
	// {
	// state.render(rm);
	// }
	// }
	// }

	// /**
	// * Calls render for all attached and initialized states, do not call
	// * directly.
	// */
	// public void postRender()
	// {
	// AppState[] array = getStates();
	// for (AppState state : array)
	// {
	// if (state.isEnabled())
	// {
	// state.postRender();
	// }
	// }
	// }

	/**
	 * Calls cleanup on attached states, do not call directly.
	 */
	public void cleanup()
	{
		SimulationSystem[] array = getStates();
		for (SimulationSystem state : array)
		{
			state.cleanup();
		}
	}

	@Override
	public void step(SimulationScheduler scheduler, float deltaTime)
	{
		this.update(deltaTime);
	}

	@Override
	public void addAll(Iterable<Class<? extends SimulationSystem>> systems)
	{
		this.attachAll(systems);
		
	}
}

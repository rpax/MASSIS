package com.massisframework.massis.sim.ecs.zayes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
import com.jme3.util.SafeArrayList;
import com.massisframework.massis.sim.ecs.CollectionsFactory;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.SimulationSystemCreator;
import com.massisframework.massis.sim.ecs.SystemsManager;
@SuppressWarnings({"unchecked","rawtypes"})
class SystemsManagerImpl implements SystemsManager {

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

	private SimulationSystemCreator systemsCreator;

	private Set<Class> disabled;

	@Inject
	public SystemsManagerImpl(SimulationSystemCreator systemsCreator)
	{
		this.systemsCreator = systemsCreator;
		this.disabled = CollectionsFactory.newSet(Class.class);
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

			if (!hasSystem(stateType))
			{
				SimulationSystem state = systemsCreator.createSystem(stateType);
				System.out.println("creating " + state);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.massisframework.massis.sim.ecs.zayes.SystemsManager#getState(java.
	 * lang.Class)
	 */

	@Override
	public <T extends SimulationSystem> T getSystem(Class<T> type)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.massisframework.massis.sim.ecs.zayes.SystemsManager#hasState(java.
	 * lang.Class)
	 */
	@Override
	public boolean hasSystem(Class<? extends SimulationSystem> stateType)
	{
		synchronized (states)
		{
			return contains(stateType, initializing)
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
	@Override
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
			if (isEnabled(state.getClass()))
			{
				state.update(tpf);
			}
		}
	}

	public boolean isEnabled(Class<? extends SimulationSystem> state)
	{
		return this.hasSystem(state) && this.disabled.contains(state);
	}

	public void setEnabled(Class<? extends SimulationSystem> state,
			boolean enable)
	{
		if (enable)
		{
			this.disabled.remove(state);
		} else if (this.hasSystem(state))
		{
			this.disabled.add(state);
		}
	}

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
	public void addAll(Iterable<Class<? extends SimulationSystem>> systems)
	{
		this.attachAll(systems);

	}

	@Override
	public void add(Class<? extends SimulationSystem> system)
	{
		this.attach(system);
	}

	@Override
	public Collection<SimulationSystem> getActiveSystems()
	{
		return Collections.unmodifiableCollection(this.states);
	}
}

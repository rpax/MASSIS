package com.massisframework.massis.sim.ecs.ashley;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.massisframework.massis.sim.SimulationScheduler;
import com.massisframework.massis.sim.SimulationSteppable;
import com.massisframework.massis.sim.ecs.ComponentConfiguration;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.injection.SystemCreator;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.ints.IntHash.Strategy;

public class AshleySimulationEngine
		implements SimulationEngine, SimulationSteppable {

	private Engine ashleyEngine;

	private Map<Integer, AshleySimulationEntity> entityIdMap;
	private SimulationScheduler scheduler;

	private List<SimulationSystem> waiting;
	private List<SimulationSystem> running;
	private List<SimulationSystem> paused;
	private List<SimulationSystem> terminating;

	private SystemCreator systemCreator;

	@Deprecated
	@Inject
	Injector injector;

	@Inject
	public AshleySimulationEngine(
			ComponentConfiguration config,
			SimulationScheduler scheduler,
			SystemCreator systemCreator)
	{
		this.ashleyEngine = new Engine();
		this.entityIdMap = createEntityIdMap();
		this.scheduler = scheduler;
		this.systemCreator = systemCreator;

		this.waiting = new ArrayList<>();
		this.running = new ArrayList<>();
		this.paused = new ArrayList<>();
		this.terminating = new ArrayList<>();

		System.out.println("Created " + this.getClass());
	}

	@Override
	public SimulationEntity asSimulationEntity(int id)
	{
		return this.entityIdMap.get(id);
	}

	@Override
	public int createEntity()
	{
		AshleySimulationEntity simEntity = injector
				.getInstance(AshleySimulationEntity.class);
		this.entityIdMap.put(simEntity.getId(), simEntity);
		this.ashleyEngine.addEntity(simEntity.getEntity());
		return simEntity.getId();
	}

	@Override
	public void destroyEntity(int eId)
	{
		AshleySimulationEntity sE = this.entityIdMap.remove(eId);
		if (sE != null)
		{
			this.ashleyEngine.removeEntity(sE.getEntity());
		}
	}

	private static Map<Integer, AshleySimulationEntity> createEntityIdMap()
	{
		return new Int2ObjectOpenCustomHashMap<>(new Strategy() {
			@Override
			public int hashCode(int e)
			{
				return SimulationEntity.fmix32(e);
			}

			@Override
			public boolean equals(int a, int b)
			{
				return a == b;
			}
		});
	}

	private static boolean containsType(Collection<?> c, Class<?> type)
	{
		return c.stream().filter(type::isInstance).findAny().isPresent();
	}

	private static Optional<? extends SimulationSystem> getOfType(
			Collection<? extends SimulationSystem> c,
			Class<? extends SimulationSystem> type)
	{
		return c.stream().filter(type::isInstance).findAny();
	}

	public boolean containsSystem(Class<? extends SimulationSystem> type)
	{
		return containsType(this.waiting, type)
				|| containsType(this.running, type);
	}

	@Override
	public void addSystem(Class<? extends SimulationSystem> type)
	{
		synchronized (this.running)
		{
			SimulationSystem system = null;
			if (!containsSystem(type))
			{
				system = this.systemCreator.createSystem(type);
				system.addedToEngine(this);
			} else
			{
				throw new IllegalArgumentException(
						"System already in the engine");
			}
			this.waiting.add(system);
		}
	}

	@Override
	public void removeSystem(Class<? extends SimulationSystem> type)
	{
		throw new UnsupportedOperationException();
	}

	private void waitingToRunning()
	{
		if (waiting.size() == 0)
			return;
		SimulationSystem[] systems = this.waiting
				.toArray(new SimulationSystem[] {});
		this.waiting.clear();
		Arrays.stream(systems).forEach(this.running::add);
		Arrays.stream(systems).forEach(SimulationSystem::initialize);
	}

	private void terminatingToDeleted()
	{
		if (terminating.size() == 0)
			return;
		this.terminating.forEach(s -> s.removedFromEngine(this));
		this.terminating.clear();
	}

	@Override
	public void start()
	{
		this.scheduler.start();
		// Needed due to Ashley's architecture
		this.ashleyEngine.addSystem(new EntitySystem() {
		});
		this.scheduler.scheduleRepeating(this);
	}

	@Override
	public void stop()
	{

		this.scheduler.removeFromSchedule(this);
	}

	@Override
	public void step(SimulationScheduler scheduler, float deltaTime)
	{
		this.terminatingToDeleted();
		this.waitingToRunning();
		for (SimulationSystem s : running)
		{
			s.update(deltaTime);
			// Needed due to Ashley's architecture
			this.ashleyEngine.update(-1);
		}
	}

	@Override
	public void getEntitiesFor(ComponentFilter filter)
	{
		this.ashleyEngine.getEntities().forEach(e -> {
			int id = e.getComponent(AshleyEntityIdReference.class).ashleyId;
			boolean m = filter.matches(this.asSimulationEntity(id));
			System.out.println("Entity #" + id + ",matches(" + m
					+ "), with components " + e.getComponents() + "");
		});

	}
}

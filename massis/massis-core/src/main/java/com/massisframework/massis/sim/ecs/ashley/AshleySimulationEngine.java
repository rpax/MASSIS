package com.massisframework.massis.sim.ecs.ashley;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.massisframework.massis.sim.SimulationScheduler;
import com.massisframework.massis.sim.SimulationSteppable;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.injection.SystemCreator;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.ints.IntHash.Strategy;

public class AshleySimulationEngine
		implements SimulationEngine<AshleySimulationEntity>, SimulationSteppable {

	private Engine ashleyEngine;

	private Map<Integer, AshleySimulationEntity> entityIdMap;

	private Queue<Runnable> taskQueue = new ConcurrentLinkedDeque<>();
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

	}

	@Override
	public AshleySimulationEntity asSimulationEntity(int id)
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

		for (SimulationSystem ss : this.running)
		{
			ss.onEntityAdded(simEntity.getId());
		}

		return simEntity.getId();
	}

	@Override
	public void destroyEntity(int eId)
	{
		AshleySimulationEntity sE = this.entityIdMap.remove(eId);
		if (sE != null)
		{
			this.ashleyEngine.removeEntity(sE.getEntity());
			for (SimulationSystem ss : this.running)
			{
				ss.onEntityRemoved(eId);
			}
		}

	}

	private static Map<Integer, AshleySimulationEntity> createEntityIdMap()
	{
		return new Int2ObjectOpenCustomHashMap<>(new Strategy() {
			@Override
			public int hashCode(int h)
			{
				h ^= h >>> 16;
				h *= 0x85ebca6b;
				h ^= h >>> 13;
				h *= 0xc2b2ae35;
				h ^= h >>> 16;
				return h;
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
				system.onAdded();
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
		Arrays.stream(systems).forEach(SimulationSystem::initialize);
		Arrays.stream(systems).forEach(this.running::add);
	}

	private void terminatingToDeleted()
	{
		if (terminating.size() == 0)
			return;
		this.terminating.forEach(s -> s.onRemoved());
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
			while (!this.taskQueue.isEmpty())
				this.taskQueue.poll().run();
			s.update(deltaTime);
			// Needed due to Ashley's architecture
			this.ashleyEngine.update(-1);
		}
	}

	@Override
	public List<SimulationEntity<?>> getEntitiesFor(ComponentFilter filter,
			List<SimulationEntity<?>> store)
	{
		if (store == null)
			store = new ArrayList<>();
		else
			store.clear();
		for (Entity e : this.ashleyEngine
				.getEntitiesFor(((AshleyComponentFilter) filter).getFamily()))
		{
			store.add(e.getComponent(AshleySimulationEntityReference.class)
					.getReference());
		}
		return store;
	}

	@Override
	public <T extends SimulationSystem> T getSystem(Class<T> type)
	{
		return running
				.stream()
				.filter(s -> type.isInstance(s)).findAny()
				.map(type::cast)
				.orElse(null);

	}
}

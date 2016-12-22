package com.massisframework.massis.sim.engine.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.spi.LinkedKeyBinding;
import com.massisframework.massis.model.components.SimulationComponent;
import com.massisframework.massis.sim.SimulationEntity;
import com.massisframework.massis.sim.engine.SimulationEngine;
import com.massisframework.massis.sim.engine.SimulationSystem;
import com.massisframework.massis.sim.engine.base.DefaultComponentModule;
import com.massisframework.massis.sim.engine.base.SystemsModule;

import cern.colt.Arrays;

public class SimulationEngineImpl implements SimulationEngine {

	private AtomicBoolean running = new AtomicBoolean(false);
	private Map<Class<? extends SimulationSystem>, EntitySystemWrapper> systems;
	private PooledEngine engine;
//	private InterfaceTypeMapper typeMapper;
	private Pool<AshleyEntityWrapper> entityPool;
	private int entityPoolInitialSize = 1024;
	private int entityPoolMaxSize = Integer.MAX_VALUE;
	private int componentPoolInitialSize = 16;
	private int componentPoolMaxSize = 1024;
	private Injector injector;
	private Runnable loop;
	private ScheduledExecutorService executor;

	private static final float FIXED_TPF = 1f / 60f;

	public SimulationEngineImpl()
	{
		this.systems = new HashMap<>();
		this.injector = Guice.createInjector(
				new DefaultComponentModule(),
				new SystemsModule());

		this.loop = this::update;

		this.engine = new PooledEngine(
				entityPoolInitialSize,
				entityPoolMaxSize,
				componentPoolInitialSize,
				componentPoolMaxSize);
//		this.typeMapper = new InterfaceTypeMapper();
		this.entityPool = createEntityPool();
		this.addListeners();

	}

	private Pool<AshleyEntityWrapper> createEntityPool()
	{
		return new Pool<AshleyEntityWrapper>() {
			@Override
			protected AshleyEntityWrapper newObject()
			{
				return new DefaultSimulationEntity();
			}
		};
	}

	public <T extends SimulationComponent> T newComponent(Class<T> type)
	{
		return this.injector.getInstance(type);
	}

	private void addListeners()
	{
//		this.engine.addEntityListener(this.typeMapper);
	}

	@Override
	public <T extends SimulationSystem> void registerSystem(Class<T> type)
	{
		synchronized (this.systems)
		{
			if (this.systems.containsKey(type))
			{
				Logger.getLogger(getClass().getName())
						.warn("Systems cannot be registered twice");
			} else
			{
				T system = injector.getInstance(type);// this.getInstance(type);
				EntitySystemWrapper wrapper = new EntitySystemWrapper(this,
						system);
				this.systems.put(system.getClass(), wrapper);
				this.engine.addSystem(wrapper);
			}
		}
	}

	public <T extends SimulationSystem> void unregisterSystem(Class<T> type)
	{
		synchronized (this.systems)
		{
			EntitySystemWrapper wrapper = this.systems.remove(type);
			this.engine.removeSystem(wrapper);

		}
	}

	@Override
	public SimulationEntity createEntity()
	{
		AshleyEntityWrapper simEntity = this.entityPool.obtain();
		simEntity.setEntity(this.engine.createEntity());
		return simEntity;
	}

	@Override
	public void removeEntity(SimulationEntity simEntity)
	{
		if (simEntity instanceof AshleyEntityWrapper)
		{
			AshleyEntityWrapper ashleyWrapper = (AshleyEntityWrapper) simEntity;
			Entity entity = ashleyWrapper.getEntity();
			ashleyWrapper.setEntity(null);
			this.entityPool.free(ashleyWrapper);
			this.engine.removeEntity(entity);
		}
	}

	@Override
	public <T extends SimulationComponent> T getComponent(
			SimulationEntity entity,
			Class<T> type)
	{
		// guarrero
		return (T) ((DefaultSimulationEntity) entity).getEntity()
				.getComponent(getBindedType(type));

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Iterable<SimulationEntity> getEntitiesFor(
			Class... types)
	{
		System.out.println(Arrays.toString(types));
		Class[] types2 = new Class[types.length];
		for (int i = 0; i < types.length; i++)
		{
			types2[i] = getBindedType(types[i]);
		}
		System.out.println(Arrays.toString(types2));
		return ((Iterable) engine.getEntitiesFor(Family.all(types2).get()));
	}

	private Class getBindedType(Class type)
	{
		return ((LinkedKeyBinding) this.injector
				.getBinding(type)).getLinkedKey().getTypeLiteral().getRawType();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends SimulationSystem> T getSystem(Class<T> type)
	{
		return (T) this.systems.get(type);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T extends SimulationSystem> void registerSystems(Class... systems)
	{
		for (Class<T> type : systems)
		{
			this.registerSystem(type);
		}

	}

	@Override
	public void start()
	{
		if (!this.running.getAndSet(true))
		{
			this.executor = Executors.newSingleThreadScheduledExecutor();
			this.executor.scheduleAtFixedRate(loop, 0,
					(long) (FIXED_TPF * 1000), TimeUnit.MILLISECONDS);
		} else
		{
			Logger.getLogger(getClass().getName())
					.warn("Engine already started");
		}
	}

	private void update()
	{
		this.engine.update(FIXED_TPF);
	}

	@Override
	public void shutdown()
	{
		this.executor.shutdownNow();
	}
}

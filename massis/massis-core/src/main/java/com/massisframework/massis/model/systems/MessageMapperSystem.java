package com.massisframework.massis.model.systems;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.injection.components.MessageHandler;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class MessageMapperSystem implements SimulationSystem {

	private Map<Class<?>, Map<Class<?>, Method>> methodMap = new HashMap<>();

	@Inject
	private EventBus eventBus;
	@Inject
	private SimulationEngine<?> engine;

	private Map<Integer, EntityMessageHandler> handlerMap = new Int2ObjectOpenHashMap<>();

	@Override
	public void initialize()
	{
	}

	@Override
	public void update(float deltaTime)
	{

	}

	private Map<Class<?>, Method> getMethods(Class<?> type)
	{
		Map<Class<?>, Method> methods = this.methodMap.get(type);
		if (methods == null)
		{
			methods = new HashMap<>();
			for (Method m : type.getDeclaredMethods())
			{

				if (m.getAnnotation(MessageHandler.class) != null)
				{
					m.setAccessible(true);
					methods.put(m.getParameters()[0].getType(), m);
				}
			}
			this.methodMap.put(type, methods);
		}
		return methods;
	}

	public void onEntityAdded(int entityId)
	{
		EntityMessageHandler mh = new EntityMessageHandler(entityId);
		this.handlerMap.put(entityId, mh);
		this.eventBus.register(mh);
	}

	public void onEntityRemoved(int entityId)
	{
		EntityMessageHandler mh = this.handlerMap.remove(entityId);
		if (mh != null)
		{
			this.eventBus.unregister(mh);
		}
	}

	private class EntityMessageHandler {

		private int entityId;

		public EntityMessageHandler(int entityId)
		{
			this.entityId = entityId;
		}

		@Subscribe
		public void onMessage(Object message)
		{

			SimulationEntity<?> se = engine.asSimulationEntity(entityId);
			Class<?> messageClass = message.getClass();
			for (SimulationComponent sc : se.getComponents())
			{
				Class<? extends SimulationComponent> type = sc.getClass();
				getMethods(type)
						.entrySet()
						.stream()
						.filter(e -> e.getKey().isAssignableFrom(messageClass))
						.map(e -> e.getValue())
						.forEach(m -> invokeMethod(sc, m, message));

			}
		}

		private void invokeMethod(SimulationComponent sc, Method m,
				Object param)
		{
			try
			{
				m.invoke(sc, param);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

}

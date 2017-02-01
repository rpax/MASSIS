package com.massisframework.massis.sim.ecs.ashley;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.Family.Builder;
import com.google.inject.Inject;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.ComponentFilterBuilder;
import com.massisframework.massis.sim.ecs.injection.SimulationConfiguration;
import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;

public class AshleyComponentFilterBuilder implements ComponentFilterBuilder {

	private SimulationConfiguration config;
	private Builder builder;
	private static Map<Family, ComponentFilter> fmap = new ConcurrentHashMap<>();

	@Inject
	public AshleyComponentFilterBuilder(SimulationConfiguration config)
	{
		this(config, false);
	}

	public AshleyComponentFilterBuilder(
			SimulationConfiguration config,
			boolean dummy)
	{
		this.config = config;
		Constructor<Builder> constructor;
		try
		{
			constructor = Builder.class
					.getDeclaredConstructor(new Class[0]);
			constructor.setAccessible(true);
			this.builder = constructor.newInstance(new Object[0]);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}

	}

	public void reset()
	{
		this.builder.reset();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ComponentFilterBuilder exclude(
			Class... componentTypes)
	{
		Class<? extends SimulationComponent>[] r = replacement(componentTypes);

		this.builder.exclude(r);

		return this;
	}

	@SuppressWarnings("unchecked")
	@SafeVarargs
	@Override
	public final ComponentFilterBuilder one(
			Class... componentTypes)
	{
		Class<? extends SimulationComponent>[] r = replacement(componentTypes);

		this.builder.one(r);

		return this;
	}

	@Override
	@SafeVarargs
	public final ComponentFilterBuilder all(
			Class... componentTypes)
	{
		Class<? extends SimulationComponent>[] r = replacement(componentTypes);

		this.builder.all(r);

		return this;
	}

	@Override
	public ComponentFilter get()
	{
		Family f = builder.get();
		ComponentFilter cf = fmap.get(f);
		if (cf == null)
		{
			cf = new AshleyComponentFilter(f);
			fmap.put(f, cf);
		}
		return cf;
	}

	// TODO optimize
	@SuppressWarnings("unchecked")
	private Class<? extends SimulationComponent>[] replacement(
			Class<? extends SimulationComponent>[] componentTypes)
	{
		@SuppressWarnings("rawtypes")
		Class[] replacement = new Class[componentTypes.length];
		for (int i = 0; i < replacement.length; i++)
		{
			replacement[i] = config.getBinding(componentTypes[i], true);

		}
		return replacement;
	}
}

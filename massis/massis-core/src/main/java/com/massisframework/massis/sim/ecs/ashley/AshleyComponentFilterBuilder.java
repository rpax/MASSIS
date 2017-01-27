package com.massisframework.massis.sim.ecs.ashley;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.Family.Builder;
import com.google.inject.Inject;
import com.massisframework.massis.sim.ecs.ComponentConfiguration;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.ComponentFilterBuilder;
import com.massisframework.massis.sim.ecs.SimulationComponent;

//@Singleton
public class AshleyComponentFilterBuilder implements ComponentFilterBuilder {

	private ComponentConfiguration config;
	private Builder builder;
	private static Map<Family, ComponentFilter> fmap = new ConcurrentHashMap<>();

	@Inject
	public AshleyComponentFilterBuilder(ComponentConfiguration config)
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

	public ComponentFilterBuilder exclude(
			Class<? extends SimulationComponent>[] componentTypes)
	{
		Class<? extends SimulationComponent>[] r = replacement(componentTypes);

		this.builder.exclude(r);

		return this;
	}

	public ComponentFilterBuilder one(
			Class<? extends SimulationComponent>[] componentTypes)
	{
		Class<? extends SimulationComponent>[] r = replacement(componentTypes);

		this.builder.one(r);

		return this;
	}

	public ComponentFilterBuilder all(
			Class<? extends SimulationComponent>... componentTypes)
	{
		Class<? extends SimulationComponent>[] r = replacement(componentTypes);

		this.builder.all(r);

		return this;
	}
	
	public ComponentFilter get()
	{
		Family f = builder.get();
		ComponentFilter cf = fmap.get(f);
		if (cf == null)
		{
			cf = (se) -> f.matches(((AshleySimulationEntity) se).getEntity());
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
			replacement[i] = config.getMapping(componentTypes[i]);
		}
		return replacement;
	}
}

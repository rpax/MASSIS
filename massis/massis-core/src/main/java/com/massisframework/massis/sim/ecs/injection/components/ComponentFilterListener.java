package com.massisframework.massis.sim.ecs.injection.components;

import com.google.inject.spi.*;
import com.massisframework.massis.sim.FilterParams;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.ComponentFilterBuilder;
import com.massisframework.massis.sim.ecs.ashley.AshleyComponentFilterBuilder;
import com.massisframework.massis.sim.ecs.injection.SimulationConfiguration;

import java.lang.reflect.*;

import com.google.inject.*;

public class ComponentFilterListener implements TypeListener {

	private SimulationConfiguration config;

	public ComponentFilterListener(
			SimulationConfiguration config)
	{
		this.config = config;
	}

	public <T> void hear(TypeLiteral<T> typeLiteral,
			TypeEncounter<T> typeEncounter)
	{
		Class<?> clazz = typeLiteral.getRawType();
		while (clazz != null)
		{
			for (Field field : clazz.getDeclaredFields())
			{
				if (field.getType() == ComponentFilter.class &&
						field.isAnnotationPresent(FilterParams.class))
				{

					typeEncounter.register(
							new ComponentFilterMembersInjector<T>(field,
									config));
				}
			}
			clazz = clazz.getSuperclass();
		}
	}

	private class ComponentFilterMembersInjector<T>
			implements MembersInjector<T> {
		private final Field field;
		private final ComponentFilter filter;

		ComponentFilterMembersInjector(Field field,
				SimulationConfiguration config)
		{
			this.field = field;
			// TODO
			ComponentFilterBuilder builder = new AshleyComponentFilterBuilder(
					config, false);

			FilterParams ann = field.getAnnotation(FilterParams.class);
			if (ann.all() != null)
				builder = builder.all(ann.all());

			if (ann.none() != null)
				builder = builder.exclude(ann.none());

			if (ann.one() != null)
				builder = builder.one(ann.one());

			this.filter = builder.get();

			field.setAccessible(true);
		}

		public void injectMembers(T t)
		{
			try
			{
				field.set(t, filter);
			} catch (IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
}

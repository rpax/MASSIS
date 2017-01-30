package com.massisframework.massis.sim.ecs.injection.components;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;

public class ComponentCreatorImpl implements ComponentCreator {

	@Inject
	private Injector injector;

	@Override
	public <T extends SimulationComponent> T createComponent(
			SimulationEntity e,
			Class<T> type)
	{
		// List<SimulationComponent> componentAndDependencies = new
		// ArrayList<>();
		T rootComponent = injector.getInstance(type);
		// componentAndDependencies.add(rootComponent);
		//
		// for (Class<? extends SimulationComponent> dep : this
		// .getAllDependencies(type))
		// {
		// if (e.getComponent(dep) == null)
		// {
		// componentAndDependencies.add(injector.getInstance(type));
		// }
		// }

		// for (SimulationComponent sc : componentAndDependencies)
		// {
		// setEntityIdField(e, sc);
		// }
		setEntityIdField(e, rootComponent);
		return rootComponent;
	}

	private void setEntityIdField(SimulationEntity e, SimulationComponent sc)
	{
		for (Field f : getFieldsByAnnotation(sc.getClass(), EntityId.class))
		{
			setFieldValue(f, sc, e.getId());
		}
		for (Field f : getFieldsByAnnotation(sc.getClass(),
				EntityReference.class))
		{
			setFieldValue(f,sc,e);
		}
	}

	private static void setFieldValue(Field f, Object target, Object value)
	{
		try
		{
			f.setAccessible(true);
			f.set(target, value);
		} catch (IllegalArgumentException | IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static boolean isValidReferenceField(Field field)
	{
		return field.getType() == SimulationComponent.class
				&& field.isAnnotationPresent(ComponentReference.class);
	}

	private Collection<Class<? extends SimulationComponent>> getAllDependencies(
			Class<?> rootType)
	{
		Set<Class<? extends SimulationComponent>> inspected = new HashSet<>();
		Queue<Class<?>> queue = new LinkedList<>();
		queue.add(rootType);
		while (!queue.isEmpty())
		{
			Class<?> type = queue.poll();
			for (Field field : type.getDeclaredFields())
			{
				if (isValidReferenceField(field))
				{
					if (!inspected.contains(field.getType()))
					{
						inspected
								.add((Class<? extends SimulationComponent>) field
										.getType());
						queue.add(field.getType());
					}
				}
			}
		}
		return inspected;

	}

	private Field[] getFieldsByAnnotation(Class<?> type,
			Class<? extends Annotation> annotationClass)
	{
		return Arrays.stream(type.getDeclaredFields())
				.filter(f -> f.getAnnotation(annotationClass) != null)
				.toArray(s -> new Field[s]);
	}

}

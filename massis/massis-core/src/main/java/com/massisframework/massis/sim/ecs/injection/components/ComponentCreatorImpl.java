package com.massisframework.massis.sim.ecs.injection.components;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.massisframework.massis.sim.SimulationScheduler;
import com.massisframework.massis.sim.SimulationSteppable;
import com.massisframework.massis.sim.ecs.OLDSimulationEntity;
import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;

public class ComponentCreatorImpl<E extends OLDSimulationEntity<E>>
		implements ComponentCreator<E> {

	@Inject
	private Injector injector;

	@Inject
	private SimulationScheduler scheduler;

	@Override
	public <T extends SimulationComponent> T createComponent(
			E e,
			Class<T> type)
	{
		// List<SimulationComponent> componentAndDependencies = new
		// ArrayList<>();
		T rootComponent = injector.getInstance(type);

		setEntityIdField(e, rootComponent);
		if (SimulationSteppable.class
				.isAssignableFrom(rootComponent.getClass()))
		{
			this.scheduler
					.scheduleRepeating((SimulationSteppable) rootComponent);
		}
		return rootComponent;
	}

	private void setEntityIdField(OLDSimulationEntity<?> e, SimulationComponent sc)
	{

		for (Field f : getFieldsByAnnotation(sc.getClass(),
				EntityReference.class))
		{
			setFieldValue(f, sc, e);
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

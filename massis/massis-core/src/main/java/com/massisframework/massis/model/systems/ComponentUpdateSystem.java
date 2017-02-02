package com.massisframework.massis.model.systems;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.inject.Inject;
import com.massisframework.massis.sim.ecs.ComponentChangeListener;
import com.massisframework.massis.sim.ecs.ComponentUpdate;
import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEntityData;
import com.massisframework.massis.sim.ecs.SimulationSystem;

@SuppressWarnings("rawtypes")
public class ComponentUpdateSystem
		implements SimulationSystem,
		ComponentChangeListener {

	private Map<Class, Method> methodMap;
	private List<ComponentInvokerPair> components;

	private SimulationEntityData ed;

	@Inject
	public ComponentUpdateSystem(SimulationEntityData ed)
	{
		this.ed = ed;
		this.ed.addComponentChangeListener(this);
	}

	@Override
	public void initialize()
	{

		this.methodMap = new HashMap<>();
		this.components = new ArrayList<>();
	}

	@Override
	public void update(float deltaTime)
	{
		for (ComponentInvokerPair invokers : components)
		{
			invokers.invoke(deltaTime);
		}
	}

	@Override
	public void componentInserted(SimulationComponent cmp)
	{
		Objects.requireNonNull(cmp);
		Class<? extends SimulationComponent> type = cmp.getClass();
		if (!this.methodMap.containsKey(type))
		{
			Method updateMethod = findUpdateMethod(type);
			methodMap.put(type, updateMethod);
		}
		Method m = this.methodMap.get(type);
		if (m != null)
		{
			this.components.add(new ComponentInvokerPair(cmp, m));
		}

	}

	private Method findUpdateMethod(Class<?> type)
	{
		Method updateMethod = null;
		for (Method m : type.getDeclaredMethods())
		{
			if (m.isAnnotationPresent(ComponentUpdate.class))
			{
				if (updateMethod != null)
				{
					throw new IllegalStateException(
							"A component cannot have more than one method annotated with @"
									+ ComponentUpdate.class.getSimpleName());
				} else
				{
					updateMethod = m;
				}
			}
		}
		return updateMethod;
	}

	@Override
	public void componentRemoved(SimulationComponent cmp)
	{
		Objects.requireNonNull(cmp);
		this.components.removeIf(invoker -> invoker.component == cmp);
	}

	private static class ComponentInvokerPair {
		SimulationComponent component;
		Method method;

		public ComponentInvokerPair(
				SimulationComponent component,
				Method method)
		{
			this.component = component;
			this.method = method;
		}

		public void invoke(float deltaTime)
		{
			try
			{
				this.method.invoke(this.component, deltaTime);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e)
			{
				if (e.getCause() instanceof RuntimeException)
				{
					throw ((RuntimeException) e.getCause());
				} else
				{
					throw new RuntimeException(e);
				}
			}
		}

	}

}

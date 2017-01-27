package com.massisframework.massis.sim.ecs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ComponentConfigurationBuilder {
	private ComponentConfigurationImpl config;

	private ComponentConfigurationBuilder()
	{
		this.config = new ComponentConfigurationImpl();
	}

	public static ComponentConfigurationBuilder builder()
	{
		return new ComponentConfigurationBuilder();
	}

	public <C extends I, I extends SimulationComponent> ComponentConfigurationBuilder bind(
			Class<I> type, Class<C> impl)
	{
		this.config.map.put(type, impl);
		return this;
	}

	public ComponentConfiguration build()
	{
		return this.config;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static class ComponentConfigurationImpl
			implements ComponentConfiguration {

		private Map<Class, Class> map = new HashMap<>();

		@Override
		public Set<Class<? extends SimulationComponent>> getRegisteredComponents()
		{
			return Collections.unmodifiableSet(this.map.keySet());
		}

		@Override
		public Class<? extends SimulationComponent> getMapping(
				Class<? extends SimulationComponent> itf)
		{
			return this.map.get(itf);
		}

	}
}
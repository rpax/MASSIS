package com.massisframework.massis.sim.ecs.zayes;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.massisframework.massis.sim.SimulationScheduler;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.injection.SimulationConfiguration;

public class InterfaceBindings {

	private Map<Class<? extends SimulationEntityComponent>, Class<? extends SimulationEntityComponent>> bindings = new HashMap<>();

	public static InterfaceBindingsBuilder builder()
	{
		return new InterfaceBindingsBuilder();
	}

	public Map<Class<?>, Class<?>> getBindings()
	{
		return Collections.unmodifiableMap(this.bindings);
	}

	public <I extends SimulationEntityComponent, C extends I> Class<C> getBinding(
			Class<I> type)
	{
		return getBinding(type, true);
	}

	@SuppressWarnings("unchecked")
	public <I extends SimulationEntityComponent, C extends I> Class<C> getBinding(
			Class<I> type, boolean loadDefaultIfNotExists)
	{
		if (loadDefaultIfNotExists)
		{
			return (Class<C>) this.bindings.getOrDefault(type, type);
		} else
		{
			return (Class<C>) this.bindings.get(type);
		}
	}

	public interface SimulationConfigurationBuilder {

		<I extends SimulationEntityComponent> ToBindingBuilder<I> map(
				Class<I> source);

		SimulationConfigurationBuilder withEngine(
				Class<? extends SimulationEngine<?>> type);

		SimulationConfigurationBuilder withScheduler(
				Class<? extends SimulationScheduler> type);

		SimulationConfigurationBuilder withSystem(
				Class<? extends SimulationSystem> system);

		SimulationConfiguration build(File buildingFile);
	}

	public interface ToBindingBuilder<I extends SimulationEntityComponent> {
		InterfaceBindingsBuilder to(Class<? extends I> target);
	}

	public static class InterfaceBindingsBuilder {

		private InterfaceBindings config = new InterfaceBindings();

		public <I extends SimulationEntityComponent> ToBindingBuilder<I> map(
				final Class<I> source)
		{
			return target -> {
				config.bindings.put(source, target);
				return this;
			};
		}

		public InterfaceBindings build()
		{
			return this.config;
		}

	}
}

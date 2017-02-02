package com.massisframework.massis.sim.ecs;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.massisframework.massis.sim.SimulationScheduler;

public class InterfaceBindings {

	private Map<Class<? extends SimulationComponent>, Class<? extends SimulationComponent>> bindings = new HashMap<>();
	private Set<Class<? extends SimulationSystem>> systems = new HashSet<>();
	public String buildingPath;

	public static InterfaceBindingsBuilder builder()
	{
		return new InterfaceBindingsBuilder();
	}

	public Map<Class<? extends SimulationComponent>, Class<? extends SimulationComponent>> getBindings()
	{
		return Collections.unmodifiableMap(this.bindings);
	}

	public <I extends SimulationComponent, C extends I> Class<C> getBinding(
			Class<I> type)
	{
		return getBinding(type, true);
	}

	@SuppressWarnings("unchecked")
	public <I extends SimulationComponent, C extends I> Class<C> getBinding(
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

		<I extends SimulationComponent> ToBindingBuilder<I> map(
				Class<I> source);

		SimulationConfigurationBuilder withScheduler(
				Class<? extends SimulationScheduler> type);

		SimulationConfigurationBuilder withSystem(
				Class<? extends SimulationSystem> system);
	}

	public interface ToBindingBuilder<I extends SimulationComponent> {
		InterfaceBindingsBuilder to(Class<? extends I> target);
	}

	public static class InterfaceBindingsBuilder {

		private InterfaceBindings config = new InterfaceBindings();

		public <I extends SimulationComponent> ToBindingBuilder<I> map(
				final Class<I> source)
		{
			return target -> {
				config.bindings.put(source, target);
				return this;
			};
		}

		public <T extends SimulationSystem> InterfaceBindingsBuilder withSystem(
				Class<T> type)
		{
			config.systems.add(type);
			return this;
		}

		public InterfaceBindings build()
		{
			return this.config;
		}

		public InterfaceBindingsBuilder withBuilding(String buildingPath)
		{
			this.config.buildingPath = buildingPath;
			return this;
		}

	}

	public Iterable<Class<? extends SimulationSystem>> getSystems()
	{
		return this.systems;

	}

	public String getBuildingPath()
	{
		return this.buildingPath;
	}

}

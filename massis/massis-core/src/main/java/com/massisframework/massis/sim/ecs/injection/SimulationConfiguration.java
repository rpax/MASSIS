package com.massisframework.massis.sim.ecs.injection;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.massisframework.massis.sim.SimulationScheduler;
import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationSystem;

public class SimulationConfiguration {

	private Map<Class<? extends SimulationComponent>, Class<? extends SimulationComponent>> bindings = new HashMap<>();
	private Class<? extends SimulationScheduler> scheduler;
	private Class<? extends SimulationEngine> engine;
	private Collection<Class<? extends SimulationSystem>> systems = new HashSet<>();
	private File buildingFile;

	public static SimulationConfigurationBuilder builder()
	{
		return new SimulationConfigurationBuilderImpl();
	}

	public Map<Class<?>, Class<?>> getBindings()
	{
		return Collections.unmodifiableMap(this.bindings);
	}

	public Collection<Class<? extends SimulationSystem>> getSystems()
	{
		return Collections.unmodifiableCollection(systems);
	}

	public Class<? extends SimulationScheduler> getShedulerType()
	{
		return this.scheduler;
	}

	public Class<? extends SimulationEngine> getEngineType()
	{
		return this.engine;
	}
	public <I extends SimulationComponent, C extends I> Class<C> getBinding(
			Class<I> type){
		return getBinding(type,false);
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

		SimulationConfigurationBuilder withEngine(
				Class<? extends SimulationEngine> type);

		SimulationConfigurationBuilder withScheduler(
				Class<? extends SimulationScheduler> type);

		SimulationConfigurationBuilder withSystem(
				Class<? extends SimulationSystem> system);

		SimulationConfiguration build(File buildingFile);
	}

	public interface ToBindingBuilder<I extends SimulationComponent> {
		SimulationConfigurationBuilder to(Class<? extends I> target);
	}

	private static class SimulationConfigurationBuilderImpl
			implements SimulationConfigurationBuilder {

		SimulationConfiguration config = new SimulationConfiguration();

		@Override
		public <I extends SimulationComponent> ToBindingBuilder<I> map(
				final Class<I> source)
		{
			return target -> {
				config.bindings.put(source, target);
				return this;
			};
		}

		@Override
		public SimulationConfigurationBuilder withEngine(
				Class<? extends SimulationEngine> type)
		{

			config.engine = type;
			return this;
		}

		@Override
		public SimulationConfigurationBuilder withScheduler(
				Class<? extends SimulationScheduler> type)
		{
			config.scheduler = type;
			return this;
		}

		@Override
		public SimulationConfiguration build(File buildingFile)
		{
			config.buildingFile = buildingFile;
			return config;
		}

		@Override
		public SimulationConfigurationBuilder withSystem(
				Class<? extends SimulationSystem> system)
		{
			config.systems.add(system);
			return this;
		}

	}

	public File getBuildingFile()
	{
		return buildingFile;
	}

}

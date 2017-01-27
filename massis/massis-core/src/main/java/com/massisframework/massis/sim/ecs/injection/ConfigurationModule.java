package com.massisframework.massis.sim.ecs.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.massisframework.massis.sim.SimulationScheduler;
import com.massisframework.massis.sim.ecs.ComponentConfiguration;
import com.massisframework.massis.sim.ecs.ComponentConfigurationBuilder;
import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.UIDProvider;
import com.massisframework.massis.sim.ecs.ashley.AshleySimulationEngine;
import com.massisframework.massis.sim.ecs.mason.MasonScheduler;

public class ConfigurationModule extends AbstractModule {

	private ComponentConfiguration[] configurations;

	public ConfigurationModule(ComponentConfiguration[] configurations)
	{
		this.configurations = configurations;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void configure()
	{
		ComponentConfigurationBuilder builder = ComponentConfigurationBuilder
				.builder();
		for (int i = 0; i < configurations.length; i++)
		{
			for (Class<? extends SimulationComponent> itf : configurations[i]
					.getRegisteredComponents())
			{
				builder.bind(itf, (Class) configurations[i].getMapping(itf));
			}
		}
		ComponentConfiguration config = builder.build();
		bind(ComponentConfiguration.class).toInstance(config);
		for (Class<? extends SimulationComponent> itf : config
				.getRegisteredComponents())
		{
			bind(itf).to((Class) config.getMapping(itf));
		}
		bind(SystemCreator.class).to(DefaultSystemCreator.class);
		bind(ComponentCreator.class).to(DefaultComponentCreator.class);

		this.configureEngines();

	}

	@Singleton
	@Provides
	public UIDProvider getUIDProvider()
	{
		return new AtomicUIDProvider();
	}

	private void configureEngines()
	{
		// TODO
		this.bind(SimulationEngine.class)
				.to(AshleySimulationEngine.class);
		this.bind(SimulationScheduler.class)
				.to(MasonScheduler.class);
	}

	@Singleton
	private static class DefaultSystemCreator implements SystemCreator {
		@Inject
		private Injector injector;

		@Override
		public <T extends SimulationSystem> T createSystem(Class<T> type)
		{
			return injector.getInstance(type);
		}
	}

	@Singleton
	private static class DefaultComponentCreator implements ComponentCreator {
		@Inject
		private Injector injector;

		@Override
		public <T extends SimulationComponent> T createComponent(Class<T> type)
		{
			return injector.getInstance(type);
		}
	}
}

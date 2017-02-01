package com.massisframework.massis.sim.ecs.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.massisframework.massis.sim.SimulationScheduler;
import com.massisframework.massis.sim.ecs.ComponentFilterBuilder;
import com.massisframework.massis.sim.ecs.OLDSimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.UIDProvider;
import com.massisframework.massis.sim.ecs.ashley.AshleyComponentFilterBuilder;
import com.massisframework.massis.sim.ecs.injection.components.ComponentsModule;

public class ConfigurationModule extends AbstractModule {

	private SimulationConfiguration config;

	public ConfigurationModule(SimulationConfiguration config)
	{
		this.config = config;
	}

	@Override
	protected void configure()
	{

		install(new ComponentsModule(config));
		install(new EventBusModule());

		bind(SystemCreator.class)
				.to(DefaultSystemCreator.class)
				.in(Singleton.class);

		bind(UIDProvider.class)
				.to(AtomicUIDProvider.class)
				.in(Singleton.class);

		// TODO remove from here
		bind(ComponentFilterBuilder.class)
				.to(AshleyComponentFilterBuilder.class);

		bind(SimulationScheduler.class).to(config.getShedulerType())
				.in(Singleton.class);
		TypeLiterals.bindWild(SimulationEngine.class, config.getEngineType(),
				this.binder());

		bind(OLDSimulationEntity.class).to(config.getSimulationEntityType());
		// bind(SimulationEntity.class).to(simulationEntityType);

	}

	// public static ParameterizedType listOf(Type elementType) {
	// return newParameterizedType(List.class, elementType);
	// }
	private static class DefaultSystemCreator implements SystemCreator {
		@Inject
		private Injector injector;

		@Override
		public <T extends SimulationSystem> T createSystem(Class<T> type)
		{
			return injector.getInstance(type);
		}
	}

}

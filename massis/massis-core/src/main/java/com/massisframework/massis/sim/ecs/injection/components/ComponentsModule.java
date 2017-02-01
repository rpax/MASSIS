package com.massisframework.massis.sim.ecs.injection.components;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.util.Types;
import com.massisframework.massis.sim.ecs.OLDSimulationEntity;
import com.massisframework.massis.sim.ecs.injection.SimulationConfiguration;
import com.massisframework.massis.sim.ecs.injection.TypeLiterals;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ComponentsModule extends AbstractModule {

	private SimulationConfiguration config;

	public ComponentsModule(SimulationConfiguration config)
	{
		this.config = config;
	}

	@Override
	protected void configure()
	{

		this.config.getBindings().forEach((k, v) -> bind(k).to((Class) v));

		TypeLiterals.bindWild(ComponentCreator.class,
				ComponentCreatorImpl.class, this.binder()).in(Singleton.class);
		
		bind(getCC()).to(getCCI());
		
		bind(TypeLiterals.createParametrizedTL(
				ComponentCreator.class, config.getSimulationEntityType()))
						.to(ComponentCreatorImpl.class).in(Singleton.class);

		bind(SimulationConfiguration.class).toInstance(config);

		bindListener(Matchers.any(), new ComponentFilterListener(config));
	}

	private <E extends OLDSimulationEntity<E>> TypeLiteral<ComponentCreator<E>> getCC()
	{
		return (TypeLiteral<ComponentCreator<E>>) TypeLiteral
				.get(Types.newParameterizedType(ComponentCreator.class,
						config.getSimulationEntityType()));
	}
	private <E extends OLDSimulationEntity<E>> TypeLiteral<ComponentCreatorImpl<E>> getCCI()
	{
		return (TypeLiteral<ComponentCreatorImpl<E>>) TypeLiteral
				.get(Types.newParameterizedType(ComponentCreatorImpl.class,
						config.getSimulationEntityType()));
	}
	// @Inject
	// @Provides
	// @Singleton
	// public ComponentCreator getCC1(Injector injector)
	// {
	// return injector.getInstance(ComponentCreatorImpl.class);
	// }
	//
	// @Inject
	// @Provides
	// @Singleton
	// public ComponentCreator<?> getCC2(Injector injector)
	// {
	// return injector.getInstance(ComponentCreatorImpl.class);
	// }

	// @Inject
	// @Provides
	// @Singleton
	// public <E extends SimulationEntity<E>> ComponentCreator<E> getCC3(
	// Injector injector)
	// {
	// return injector.getInstance(ComponentCreatorImpl.class);
	// }

	public <E extends OLDSimulationEntity<E>> TypeLiteral<ComponentCreator<?>> createTL(
			Class<E> clazz)
	{
		return new TypeLiteral<ComponentCreator<?>>() {
		};
	}
}

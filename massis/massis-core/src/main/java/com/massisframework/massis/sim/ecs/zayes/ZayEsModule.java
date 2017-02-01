package com.massisframework.massis.sim.ecs.zayes;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class ZayEsModule extends AbstractModule {

	private InterfaceBindings interfaceBindings;

	public ZayEsModule(InterfaceBindings interfaceBindings)
	{
		this.interfaceBindings = interfaceBindings;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void configure()
	{

		this.interfaceBindings.getBindings().forEach((itf, impl) -> {
			bind(itf).to((Class) impl);
		});
		bind(SimulationEntityData.class).to(InterfaceEntityData.class);

	}

	@Provides
	@Singleton
	public InterfaceBindings getInterfaceBindings()
	{
		return this.interfaceBindings;
	}

	@Provides
	@Inject
	@Singleton
	public EntityComponentCreator getECC(Injector injector)
	{
		return new EntityComponentCreator() {

			@Override
			public <T extends SimulationEntityComponent> T create(Class<T> type)
			{
				return injector.getInstance(type);
			}

		};
	}

}

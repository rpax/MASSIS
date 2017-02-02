package com.massisframework.massis.sim.ecs.zayes;

import com.eteks.sweethome3d.io.HomeFileRecorder;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomeRecorder;
import com.eteks.sweethome3d.model.RecorderException;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.massisframework.massis.sim.ecs.SimulationSystem;

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
		bind(SimulationEntityData.class).to(InterfaceEntityData.class)
				.in(Singleton.class);
		bind(SystemsManager.class).to(SystemsManagerImpl.class)
				.in(Singleton.class);
		try
		{
			HomeRecorder recorder = new HomeFileRecorder();
			Home home = recorder
					.readHome(this.interfaceBindings.getBuildingPath());
			bind(Home.class).toInstance(home);
		} catch (RecorderException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Provides
	@Singleton
	@Inject
	public SimulationSystemCreator getSystemCreator(Injector injector)
	{
		return new SimulationSystemCreator() {

			@Override
			public SimulationSystem createSystem(
					Class<? extends SimulationSystem> stateType)
			{
				return injector.getInstance(stateType);
			}
		};
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
			public <T extends SimulationComponent> T create(Class<T> type)
			{
				return injector.getInstance(type);
			}

		};
	}

}

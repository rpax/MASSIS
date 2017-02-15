package com.massisframework.massis.sim.ecs.injection;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class EventBusModule extends AbstractModule {

	@Override
	protected void configure()
	{
		// Listener para subscribir o des-subscirbir?
	}

	@Provides
	@Singleton
	public EventBus getEventBus()
	{
		return new EventBus("Global Event Bus");
	}
}

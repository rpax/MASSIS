package com.massisframework.massis.sim.ecs.ashley;

import com.google.inject.AbstractModule;
import com.massisframework.massis.sim.ecs.ComponentFilterBuilder;

public class AshleyModule extends AbstractModule {

	@Override
	protected void configure()
	{
		bind(ComponentFilterBuilder.class)
				.to(AshleyComponentFilterBuilder.class);	
		
	}

}

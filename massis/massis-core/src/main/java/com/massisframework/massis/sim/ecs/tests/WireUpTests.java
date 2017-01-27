package com.massisframework.massis.sim.ecs.tests;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.massisframework.massis.sim.ecs.ComponentConfiguration;
import com.massisframework.massis.sim.ecs.ComponentConfigurationBuilder;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.ashley.AshleyComponentFilterBuilder;
import com.massisframework.massis.sim.ecs.injection.ConfigurationModule;

public class WireUpTests {

	public static void main(String[] args)
	{
		ComponentConfiguration[] cc = new ComponentConfiguration[] {
				ComponentConfigurationBuilder.builder()
						.bind(NameComponent.class, NameComponentImpl.class)
						.build()
		};
		Injector injector = Guice.createInjector(new ConfigurationModule(cc));
		SimulationEngine engine = injector.getInstance(SimulationEngine.class);
		engine.addSystem(FooSystem.class);
		engine.start();

	}

	private static class FooSystem implements SimulationSystem {

		private SimulationEngine engine;
		private Provider<AshleyComponentFilterBuilder> b;
		
		@Inject
		public FooSystem(Provider<AshleyComponentFilterBuilder> b){
			this.b=b;
		}
		@Override
		public void addedToEngine(SimulationEngine engine)
		{
			System.out
					.println("FooSystem added to the engine!(" + engine + ")");
			this.engine = engine;

		}

		@Override
		public void initialize()
		{
			System.out.println("FooSystem initialized()");
			int eid = this.engine.createEntity();
			SimulationEntity e1 = this.engine.asSimulationEntity(eid);
			e1.addComponent(NameComponent.class).setName("_1");
			
		}

		@Override
		public void update(float deltaTime)
		{
			System.out.println("FooSystem update(" + deltaTime + ")");
			AshleyComponentFilterBuilder builder = b.get();
			ComponentFilter filter = builder.all(NameComponent.class).get();
			
			this.engine.getEntitiesFor(filter);
		}

		@Override
		public void removedFromEngine(SimulationEngine engine)
		{
			System.out.println("FooSystem removedFromEngine(" + engine + ")");
		}

	}
}

interface NameComponent extends SimulationComponent {
	String getName();

	String setName(String name);
}

class NameComponentImpl implements NameComponent {

	String name;

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String setName(String name)
	{
		this.name = name;
		return name;
	}

}

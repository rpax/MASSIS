package com.massisframework.massis.sim.ecs.zayes;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.simsilica.es.EntityId;

public class ZayEsTests {

	public static void main(String[] args)
	{

		InterfaceBindings bindings = InterfaceBindings.builder()
				.map(A.class).to(AImpl.class)
				.map(B.class).to(BImpl.class)
				.build();

		Injector injector = Guice.createInjector(new ZayEsModule(bindings));

		SimulationEntityData ed = injector
				.getInstance(SimulationEntityData.class);
		EntityId eid = ed.createEntity();
		ed.add(eid, A.class);
		ed.add(eid, B.class);
	
		SimulationEntitySet es = ed.createEntitySet(A.class, B.class);
		es.applyChanges();
		for (SimulationEntity e : es)
		{
			e.editC(A.class)
					.set(a -> a.uh(7))
					.set(a -> a.zz(12))
					.commit();
			e.editC(B.class)
					.set(b -> b.peter("asd"))
					.commit();
			System.out.println(e.getC(A.class));
			e.editC(A.class)
					.set(A::uh, 4)
					.set(A::setName, "")
					.commit();
		}

	}

	static interface B extends SimulationComponent {
		public default int peter(String name)
		{
			return 0;
		}
	}

	static class BImpl implements B {

		@Override
		public void reset()
		{

		}

	}

	static interface A extends SimulationComponent {
		public void uh(int a);

		public default void setName(String name)
		{
		}

		public default void ss(String a)
		{
		}

		public default void zz(int a)
		{
		}
	}

	static class AImpl implements A {

		@Override
		public void reset()
		{

		}

		@Override
		public void uh(int a)
		{
			// TODO Auto-generated method stub

		}
	}
}

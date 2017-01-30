package com.massisframework.massis.sim.ecs.injection;

import java.util.concurrent.atomic.AtomicInteger;

import com.massisframework.massis.sim.ecs.UIDProvider;

public class AtomicUIDProvider implements UIDProvider {

	private AtomicInteger id_gen = new AtomicInteger();

	@Override
	public int getNewUID()
	{
		return id_gen.getAndIncrement();
	}

}

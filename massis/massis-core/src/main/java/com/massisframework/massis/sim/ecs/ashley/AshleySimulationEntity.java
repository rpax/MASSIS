package com.massisframework.massis.sim.ecs.ashley;

import com.badlogic.ashley.core.Entity;
import com.google.inject.Inject;
import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.UIDProvider;
import com.massisframework.massis.sim.ecs.injection.ComponentCreator;

public class AshleySimulationEntity
		implements SimulationEntity {

	private ComponentCreator componentCreator;
	private int id;
	private Entity entity;

	@Inject
	public AshleySimulationEntity(
			ComponentCreator componentCreator,
			UIDProvider uidProvider)
	{
		this.id = uidProvider.getNewUID();
		this.entity = new Entity();
		entity.add(new AshleyEntityIdReference(this.id));
		this.componentCreator = componentCreator;
	}

	public Entity getEntity()
	{
		return this.entity;
	}

	@Override
	public int getId()
	{
		return this.id;
	}

	@Override
	public int hashCode()
	{
		// https://github.com/yonik/java_util/blob/master/src/util/hash/MurmurHash3.java
		int h = this.id;
		h ^= h >>> 16;
		h *= 0x85ebca6b;
		h ^= h >>> 13;
		h *= 0xc2b2ae35;
		h ^= h >>> 16;
		return h;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AshleySimulationEntity other = (AshleySimulationEntity) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public <T extends SimulationComponent> T addComponent(Class<T> type)
	{
		return (T) this.entity
				.addAndReturn(this.componentCreator.createComponent(type));
		
	}

	@Override
	public <T extends SimulationComponent> void deleteComponent(Class<T> type)
	{
		this.entity.remove(type);

	}

}

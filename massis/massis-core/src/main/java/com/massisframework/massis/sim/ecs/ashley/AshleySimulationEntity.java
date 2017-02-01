package com.massisframework.massis.sim.ecs.ashley;

import java.util.Collections;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.massisframework.massis.sim.ecs.OLDSimulationEntity;
import com.massisframework.massis.sim.ecs.UIDProvider;
import com.massisframework.massis.sim.ecs.injection.SimulationConfiguration;
import com.massisframework.massis.sim.ecs.injection.components.ComponentCreator;
import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;

public class AshleySimulationEntity
		implements OLDSimulationEntity<AshleySimulationEntity> {

	private ComponentCreator<AshleySimulationEntity> componentCreator;
	private int id;
	private Entity entity;
	private EventBus evtBus;
	private AshleySimulationEntity parent;
	private Set<AshleySimulationEntity> children;
	private SimulationConfiguration config;

	@Inject
	public AshleySimulationEntity(
			SimulationConfiguration config,
			ComponentCreator<AshleySimulationEntity> componentCreator,
			UIDProvider uidProvider,
			EventBus evtBus)
	{
		this.evtBus = evtBus;
		this.config = config;
		this.id = uidProvider.getNewUID();
		this.entity = new Entity();
		this.componentCreator = componentCreator;
		entity.add(new AshleyEntityIdReference(this.id));
		entity.add(new AshleySimulationEntityReference(this));
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
				.addAndReturn(
						this.componentCreator.createComponent(this, type));

	}

	@Override
	public <T extends SimulationComponent> void remove(Class<T> type)
	{
		this.entity.remove(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<SimulationComponent> getComponents()
	{
		return (Iterable) this.entity.getComponents();
	}

	@Override
	public void sendMessage(Object msg)
	{
		System.out.println("Posting " + msg);
		this.evtBus.post(msg);
	}

	@Override
	public <T extends SimulationComponent> T get(Class<T> type)
	{
		return this.entity.getComponent(config.getBinding(type, true));
	}

	@Override
	public Iterable<AshleySimulationEntity> getChildren()
	{
		if (this.children == null)
		{
			return Collections.emptySet();
		}
		return this.children;
	}

	@Override
	public void addChild(AshleySimulationEntity e)
	{
		if (e.parent != null)
		{
			e.parent.removeChild(e);
		}
		e.parent = this;
		this.children.add(e);
	}

	@Override
	public void removeChild(AshleySimulationEntity e)
	{
		if (this.children.remove(e))
		{
			e.parent = null;
		}
	}

	@Override
	public AshleySimulationEntity getParent()
	{
		return parent;
	}

}

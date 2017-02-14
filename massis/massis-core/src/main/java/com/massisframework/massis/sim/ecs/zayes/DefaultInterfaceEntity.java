package com.massisframework.massis.sim.ecs.zayes;

import java.lang.reflect.Field;
import java.util.Arrays;

import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationEntityData;
import com.massisframework.massis.sim.ecs.injection.components.EntityReference;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntity;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DefaultInterfaceEntity
		extends DefaultEntity
		implements SimulationEntity {

	private SimulationEntityData ed;
	private SimulationComponent[] components;
	private Class<? extends SimulationComponent>[] types;

	public DefaultInterfaceEntity(
			InterfaceEntityData ed, EntityId id,
			SimulationComponent[] components,
			Class[] types)
	{
		super(ed, id, components, types);
		this.ed = ed.getSimulationED();
		this.components = components;
		this.types = types;
		validate();
	}

	protected void validate()
	{
		if (types == null)
		{
			return;
		} else
		{
			super.validate();
		}
	}

	@Override
	public <T extends SimulationComponent> T add(T cmp)
	{
		this.fillWithEntityReference(cmp);
		this.set(cmp);
		return cmp;
	}

	@Override
	public SimulationComponent get(Class c)
	{
		return get_internal(c);
	}

	// @Override
	// public <T extends SimulationComponent> ComponentEdit<T> edit(Class<T>
	// type)
	// {
	// ComponentEditImpl<T> entityEdit = new ComponentEditImpl<>(this);
	// entityEdit.setComponent(this.get_internal(type));
	// return entityEdit;
	// }

	private <T extends SimulationComponent> T get_internal(Class<T> c)
	{
		for (int i = 0; i < types.length; i++)
		{
			if (components[i] != null && c.isAssignableFrom(types[i]))
			{
				return c.cast(components[i]);
			}
		}
		return this.ed.get(this.id(), c);
	}

	@Override
	public <T extends SimulationComponent> void remove(Class<T> type)
	{
		this.ed.remove(this.id(), type);
	}

	@Override
	public SimulationEntity getParent()
	{
		Long pId = this.get_internal(ParentComponent.class).getParentId();
		if (pId == null)
			return null;
		return this.ed.getSimulationEntity(pId);
	}

	@Override
	public Iterable<SimulationEntity> getChildren()
	{
		return this.get_internal(ChildrenComponent.class)
				.getChildren()
				.stream()
				.map(this.ed::getSimulationEntity)::iterator;
	}

	private void setRelationship(Long child, Long parent)
	{

		Long oldPId = this.ed.getSimulationEntity(child)
				.get(ParentComponent.class)
				.getParentId();
		if (oldPId != null)
		{
			SimulationEntity pEntity = this.ed.getSimulationEntity(oldPId);
			pEntity.get(ChildrenComponent.class).remove(child);
			pEntity.markChanged(ChildrenComponent.class);
		}
		if (parent != null)
		{
			SimulationEntity pEntity = this.ed.getSimulationEntity(parent);
			pEntity.get(ChildrenComponent.class).add(child);
			pEntity.markChanged(ChildrenComponent.class);
		}
		SimulationEntity pEntity = this.ed.getSimulationEntity(child);
		pEntity.get(ParentComponent.class).setParentId(parent);
		pEntity.markChanged(ChildrenComponent.class);

	}

	@Override
	public void addChild(long child)
	{
		setRelationship(child, this.id());
	}

	@Override
	public void setParent(Long parent)
	{
		setRelationship(this.id(), parent);
	}

	@Override
	public void setParent(SimulationEntity se)
	{
		this.setParent(se.id());
	}

	@Override
	public void removeFromParent()
	{
		this.setRelationship(this.id(), null);
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + "[" + id() + ", values="
				+ Arrays.asList(components) + "]";
	}

	@Override
	public long id()
	{
		return this.getId().getId();
	}

	private void fillWithEntityReference(SimulationComponent sc)
	{
		Field entityField = Arrays.stream(sc.getClass().getDeclaredFields())
				.filter(f -> f.getAnnotation(EntityReference.class) != null)
				.findAny()
				.orElse(null);
		if (entityField != null)
		{
			entityField.setAccessible(true);
			try
			{
				entityField.set(sc, this.ed);
			} catch (IllegalArgumentException | IllegalAccessException e1)
			{
				throw new RuntimeException(e1);
			}
		}
	}

	@Override
	public <T extends SimulationComponent> void markChanged(Class<T> type)
	{
		SimulationComponent cmp = this.get(type);
		if (cmp != null)
		{
			this.ed.add(this.id(), cmp);
		}
	}

}

package com.massisframework.massis.model.components.building.impl;

import java.util.ArrayList;
import java.util.List;

import com.massisframework.massis.model.components.SimulationComponent;
import com.massisframework.massis.model.components.SimulationEntity;

public class DefaultSimulationEntity implements SimulationEntity {

	private final long id;

	private List<SimulationComponent> components;

	public DefaultSimulationEntity(long id)
	{
		this.id = id;
		this.components = new ArrayList<>();
	}

	@Override
	public long getId()
	{
		return this.id;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends SimulationComponent> T get(
			Class<T> type)
	{
		for (int i = 0; i < components.size(); i++)
		{
			final SimulationComponent cmp = components.get(i);
			if (type.isInstance(cmp))
			{
				return (T) cmp;
			}
		}
		return null;
	}

	@Override
	public <T extends SimulationComponent> void set(T cmp)
	{
		// TODO check for duplicates or not?
		this.components.add(cmp);
		cmp.setEntity(this);

	}
	@Override
	public <T extends SimulationComponent> void remove(Class<T> type)
	{
		this.components.removeIf(type::isInstance);

	}
	@Override
	public <T extends SimulationComponent> boolean has(Class<T> type)
	{
		return this.get(type) != null;
	}

}

package com.massisframework.massis.sim.ecs.zayes;

import java.util.Collections;
import java.util.List;

import com.massisframework.massis.sim.ecs.CollectionsFactory;
import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.simsilica.es.EntityId;

class ChildrenComponent implements SimulationComponent {

	private List<Long> children;

	public ChildrenComponent()
	{

	}

	public List<Long> getChildren()
	{
		if (this.children == null)
			return Collections.emptyList();
		else
			return this.children;
	}

	public void add(SimulationEntity child)
	{
		this.add(child.id());
	}

	public void remove(SimulationEntity child)
	{
		this.remove(child.id());
	}

	public void add(EntityId childId)
	{
		this.add(childId.getId());
	}

	public void remove(EntityId childId)
	{
		this.remove(childId.getId());
	}

	public void add(long childId)
	{
		if (this.children == null)
		{
			this.children = CollectionsFactory.newList(Long.class);
		}
		this.children.add(childId);
	}

	public void remove(long childId)
	{
		if (this.children != null)
		{
			this.children.remove(childId);
		}
		if (this.children.size() == 0)
		{
			this.children = null;
		}
	}

}

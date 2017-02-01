package com.massisframework.massis.sim.ecs.zayes;

import com.simsilica.es.EntityId;

public class ParentComponent implements SimulationComponent {

	private EntityId parentId = null;

	public EntityId getParentId()
	{
		return parentId;
	}

	public void setParentId(EntityId parentId)
	{
		this.parentId = parentId;
	}

}

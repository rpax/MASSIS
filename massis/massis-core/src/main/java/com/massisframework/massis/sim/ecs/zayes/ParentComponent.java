package com.massisframework.massis.sim.ecs.zayes;

import com.massisframework.massis.sim.ecs.SimulationComponent;

class ParentComponent implements SimulationComponent {

	private Long parentId = null;

	public Long getParentId()
	{
		return parentId;
	}

	public void setParentId(Long parentId)
	{
		this.parentId = parentId;
	}

}

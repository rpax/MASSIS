package com.massisframework.massis.model.systems;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.ShapeComponent;
import com.massisframework.massis.model.components.TransformComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationEntityData;
import com.massisframework.massis.sim.ecs.SimulationEntitySet;
import com.massisframework.massis.sim.ecs.SimulationSystem;

public class ShapeSystem implements SimulationSystem {

	@Inject
	private SimulationEntityData ed;
	private SimulationEntitySet entities;

	@Override
	public void initialize()
	{
		this.entities = this.ed.createEntitySet(TransformComponent.class,
				ShapeComponent.class);
	}

	@Override
	public void update(float deltaTime)
	{
		if (this.entities.applyChanges())
		{
			for (SimulationEntity e : this.entities.getActiveEntities())
			{
				TransformComponent tc = e.get(TransformComponent.class);
				e.get(ShapeComponent.class).translateTo(tc.getX(), tc.getY());
				e.markChanged(ShapeComponent.class);
			}
		}
	}

}

package com.massisframework.massis.model.systems;

import com.google.inject.Inject;
import com.jme3.math.Vector2f;
import com.jme3.util.TempVars;
import com.massisframework.massis.model.components.TransformComponent;
import com.massisframework.massis.model.components.Velocity;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationEntityData;
import com.massisframework.massis.sim.ecs.SimulationEntitySet;
import com.massisframework.massis.sim.ecs.SimulationSystem;

public class VelocitySystem implements SimulationSystem {

	@Inject
	private SimulationEntityData ed;

	private SimulationEntitySet entities;

	@Override
	public void initialize()
	{
		this.entities = this.ed.createEntitySet(
				Velocity.class,
				TransformComponent.class);
	}

	@Override
	public void update(float deltaTime)
	{
		this.entities.applyChanges();
		//if (this.entities.applyChanges())
		{
			for (SimulationEntity e : this.entities)
			{
				TransformComponent transform = e.get(TransformComponent.class);
				Velocity velocity=e.get(Velocity.class);
				TempVars tmp = TempVars.get();
				Vector2f pos = transform.getPosition(new Vector2f());
				
				Vector2f offset = velocity.getValue(new Vector2f());
				offset.multLocal(deltaTime);
				pos.addLocal(offset);
				e.get(TransformComponent.class).setLocalTranslation(pos);
				e.markChanged(TransformComponent.class);
				tmp.release();

			}
		}

	}

}

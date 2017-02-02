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
		this.entities = this.ed.createEntitySet(Velocity.class,
				TransformComponent.class);
	}

	@Override
	public void update(float deltaTime)
	{
		if (this.entities.applyChanges())
		{
			for (SimulationEntity e : this.entities.getAddedEntities())
			{
				TransformComponent transform = e.getComponent(TransformComponent.class);
				TempVars tmp = TempVars.get();
				Vector2f pos = transform.getPosition(tmp.vect2d);
				Vector2f newPos = e.getComponent(Velocity.class)
						.getValue(tmp.vect2d2)
						.multLocal(deltaTime)
						.addLocal(pos);
				e.edit(TransformComponent.class)
						.set(TransformComponent::setLocalTranslation, newPos);
				tmp.release();

			}
		}

	}

}

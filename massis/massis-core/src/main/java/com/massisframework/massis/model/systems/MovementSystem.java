package com.massisframework.massis.model.systems;

import com.google.inject.Inject;
import com.jme3.math.Vector2f;
import com.massisframework.massis.model.components.MovingTo;
import com.massisframework.massis.model.components.TransformComponent;
import com.massisframework.massis.model.components.Velocity;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntity;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntityData;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntitySet;
import com.massisframework.massis.util.geom.CoordinateHolder;

public class MovementSystem implements SimulationSystem {

	@Inject
	private SimulationEntityData ed;
	private SimulationEntitySet entities;

	@Override
	public void initialize()
	{
		this.entities = ed.createEntitySet(TransformComponent.class,
				Velocity.class);
	}

	@Override
	public void update(float deltaTime)
	{
		this.entities.applyChanges();
		for (SimulationEntity e : this.entities)
		{

			CoordinateHolder target = e.getC(MovingTo.class)
					.getTarget();

			// followPath
			Vector2f newVel = new Vector2f((float) target.getX(),
					(float) target.getY())
							.subtractLocal(e.getC(TransformComponent.class)
									.getPosition(new Vector2f()))
							.normalizeLocal()
							.multLocal(100);
			e.editC(Velocity.class).set(Velocity::setValue, newVel);
		}
	}
}

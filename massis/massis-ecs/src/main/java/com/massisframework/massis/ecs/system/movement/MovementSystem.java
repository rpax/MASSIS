package com.massisframework.massis.ecs.system.movement;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.massisframework.massis.ecs.components.AIComponent;
import com.massisframework.massis.ecs.components.BuildingLocation;

public class MovementSystem extends IteratingSystem {

	public MovementSystem()
	{
		super(Aspect.all(BuildingLocation.class,AIComponent.class));
	}

	@Override
	protected void process(int entityId)
	{
		BuildingLocation loc=world.getEntity(entityId).getComponent(BuildingLocation.class);
		loc.setX(loc.getX()+world.delta*50f);
	}

}

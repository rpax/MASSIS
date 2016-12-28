package com.massisframework.massis.ecs.system.ai;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.massisframework.massis.ecs.components.AIComponent;

public class AISystem extends IteratingSystem {

	public AISystem()
	{
		super(Aspect.all(AIComponent.class));
	}

	@Override
	protected void process(int entityId)
	{
		this.world.getEntity(entityId)
				.getComponent(AIComponent.class)
				.getExecutor()
				.execute(entityId, world);
	}

}

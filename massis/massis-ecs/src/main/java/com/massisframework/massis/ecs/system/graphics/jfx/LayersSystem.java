package com.massisframework.massis.ecs.system.graphics.jfx;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.massisframework.massis.ecs.components.g2d.layer.LayerComponent;

public class LayersSystem extends BaseEntitySystem {

	public LayersSystem()
	{
		super(Aspect.all(LayerComponent.class));
	}

	@Override
	protected void initialize()
	{
		super.initialize();
		
	}

	@Override
	protected void inserted(int entityId)
	{
		super.inserted(entityId);
		//world.getSystem(FloorSceneGraphSystem.class).

	}

	@Override
	protected void removed(int entityId)
	{
		super.removed(entityId);
	}

	@Override
	protected void processSystem()
	{

	}

}

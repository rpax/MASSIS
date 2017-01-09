package com.massisframework.massis.ecs.system.graphics.jfx;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.massisframework.massis.ecs.components.BuildingLocation;
import com.massisframework.massis.ecs.components.g2d.shape.JFXShapeComponent;

public class FXUpdateSystem extends IteratingSystem {

	public FXUpdateSystem()
	{
		super(Aspect.all(JFXShapeComponent.class));
	}

	@Override
	protected void process(int entityId)
	{
		JFXShapeComponent sc = world.getEntity(entityId)
				.getComponent(JFXShapeComponent.class);
		BuildingLocation bl = world.getEntity(sc.parent)
				.getComponent(BuildingLocation.class);
		sc.getShape().translateXProperty().set(bl.getX());
		sc.getShape().translateYProperty().set(bl.getZ());

	}

}

package com.massisframework.massis.ecs.system.graphics.jfx;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.IteratingSystem;
import com.massisframework.massis.ecs.components.BuildingLocation;
import com.massisframework.massis.ecs.components.DoorOrWindowComponent;
import com.massisframework.massis.ecs.components.PolygonComponent;
import com.massisframework.massis.ecs.components.RoomComponent;
import com.massisframework.massis.ecs.components.StaticObstacle;
import com.massisframework.massis.ecs.components.WallComponent;
import com.massisframework.massis.ecs.components.g2d.JFXShapeComponent;
import com.massisframework.massis.ecs.util.SimulationObjects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class HomeObjectsFXSystem extends IteratingSystem {

	public HomeObjectsFXSystem()
	{
		super(Aspect.all(BuildingLocation.class, StaticObstacle.class)
				.one(
						RoomComponent.class,
						WallComponent.class,
						DoorOrWindowComponent.class));

	}

	@Override
	protected void process(int entityId)
	{

	}

	@Override
	protected void inserted(int entityId)
	{
		super.inserted(entityId);
		Entity entity = world.getEntity(entityId);
		JFXShapeComponent jfxNode = new JFXShapeComponent();
		Polygon jfxPoly = new Polygon();
		entity.getComponent(PolygonComponent.class).get().getPoints()
				.forEach(kp -> {
					jfxPoly.getPoints().add(kp.x);
					jfxPoly.getPoints().add(kp.y);
				});
		jfxNode.setParent(entityId);
		jfxNode.setShape(jfxPoly);
		if (SimulationObjects.isWall(entity))
		{
			//
			jfxPoly.setFill(Color.BLUE);

		} else if (SimulationObjects.isRoom(entity))
		{
			jfxPoly.setFill(Color.GRAY);
		} else if (SimulationObjects.isDoor(entity))
		{
			jfxPoly.setFill(Color.GREEN);
		}
		Entity nodeEntity = world.createEntity();
		nodeEntity.edit().add(jfxNode);
	}

	@Override
	protected void removed(int entityId)
	{
		super.removed(entityId);
	}

	@Override
	protected void initialize()
	{
		super.initialize();

	}

}

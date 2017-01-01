package com.massisframework.massis.ecs.system.graphics.jfx;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.IteratingSystem;
import com.massisframework.massis.ecs.components.BuildingLocation;
import com.massisframework.massis.ecs.components.DoorOrWindowComponent;
import com.massisframework.massis.ecs.components.DynamicObstacle;
import com.massisframework.massis.ecs.components.FurnitureComponent;
import com.massisframework.massis.ecs.components.PolygonComponent;
import com.massisframework.massis.ecs.components.RoomComponent;
import com.massisframework.massis.ecs.components.WallComponent;
import com.massisframework.massis.ecs.components.g2d.shape.JFXShapeComponent;
import com.massisframework.massis.ecs.util.SimulationObjects;
import com.massisframework.massis.javafx.util.Geometries;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

@SuppressWarnings("unchecked")
public class HomeObjectsFXSystem extends IteratingSystem {

	public HomeObjectsFXSystem()
	{
		super(Aspect.all(BuildingLocation.class)
				.one(
						RoomComponent.class,
						WallComponent.class,
						DoorOrWindowComponent.class,
						FurnitureComponent.class,
						DynamicObstacle.class));
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
		jfxNode.setParent(entityId);
		jfxNode.setShape(jfxPoly);
		if (SimulationObjects.isDynamic(entity))
		{
			Geometries.createRegularPolygon(3, entity
					.getComponent(PolygonComponent.class).get().getRadius(),
					jfxPoly);
			jfxPoly.setFill(Color.YELLOW);
			jfxPoly.setScaleX(1);
			jfxPoly.setScaleY(0.6);

		} else
		{
			entity.getComponent(PolygonComponent.class).get().getPoints()
					.forEach(kp -> {
						jfxPoly.getPoints().add(kp.x);
						jfxPoly.getPoints().add(kp.y);
					});
		}

		double x = entity.getComponent(BuildingLocation.class).getX();
		double z = entity.getComponent(BuildingLocation.class).getZ();
		jfxPoly.setTranslateX(x);
		jfxPoly.setTranslateY(z);

		if (SimulationObjects.isWall(entity))
		{
			jfxNode.setGroup("WALL");
			jfxNode.setFill(Color.BLUE);

		} else if (SimulationObjects.isRoom(entity))
		{
			jfxNode.setGroup("ROOM");
			jfxNode.setFill(Color.GRAY);
		} else if (SimulationObjects.isDoor(entity))
		{
			jfxNode.setGroup("DOOR");
			jfxNode.setFill(Color.GREEN);
		} else if (SimulationObjects.isFurniture(entity))
		{
			jfxNode.setGroup("FURNITURE");
			jfxNode.setFill(Color.BROWN);
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

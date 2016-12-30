package com.massisframework.massis.ecs.util;

import com.artemis.Entity;
import com.artemis.World;
import com.massisframework.massis.ecs.components.DoorOrWindowComponent;
import com.massisframework.massis.ecs.components.DynamicObstacle;
import com.massisframework.massis.ecs.components.FurnitureComponent;
import com.massisframework.massis.ecs.components.RoomComponent;
import com.massisframework.massis.ecs.components.WallComponent;

public final class SimulationObjects {

	private SimulationObjects()
	{
	}

	public static boolean isWall(Entity e)
	{
		return e.getComponent(WallComponent.class) != null;
	}

	public static boolean isWall(int eid, World world)
	{
		return isWall(world.getEntity(eid));
	}

	public static boolean isRoom(Entity e)
	{
		return e.getComponent(RoomComponent.class) != null;
	}

	public static boolean isRoom(int eid, World world)
	{
		return isRoom(world.getEntity(eid));
	}

	public static boolean isDoor(int eid, World world)
	{
		return isDoor(world.getEntity(eid));
	}

	public static boolean isDoor(Entity e)
	{
		return e.getComponent(DoorOrWindowComponent.class) != null;
	}

	public static boolean isDynamic(Entity e)
	{
		return e.getComponent(DynamicObstacle.class) != null;
	}

	public static boolean isDynamic(int eid, World world)
	{
		return isDynamic(world.getEntity(eid));
	}

	public static boolean isFurniture(Entity e)
	{
		return e.getComponent(FurnitureComponent.class) != null;
	}

	public static boolean isFurniture(int eid, World world)
	{
		return isFurniture(world.getEntity(eid));
	}

	
}

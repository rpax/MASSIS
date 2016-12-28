package com.massisframework.massis.ecs.components;

import java.awt.Rectangle;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.utils.IntBag;
import com.eteks.sweethome3d.model.Level;

public class Floor extends Component {

	/**
	 * @treatAsPrivate
	 */
	public Level level;
	/**
	 * @treatAsPrivate
	 */
	public @EntityId IntBag walls = new IntBag();
	/**
	 * @treatAsPrivate
	 */

	public @EntityId IntBag rooms = new IntBag();
	/**
	 * @treatAsPrivate
	 */
	public @EntityId IntBag doors = new IntBag();
	/**
	 * @treatAsPrivate
	 */
	public @EntityId IntBag dynamicEntities = new IntBag();
	/**
	 * @treatAsPrivate
	 */
	public @EntityId IntBag furniture = new IntBag();
	/*
	 * 
	 */
	/**
	 * Bounds
	 * 
	 * @treatAsPrivate
	 */
	public int minX = 0, maxX = 1, minY = 0, maxY = 1, xlength = 1,
			ylength = 1;

	public Level getLevel()
	{
		return level;
	}

	public void setLevel(Level level)
	{
		this.level = level;
	}

	public int getMinX()
	{
		return minX;
	}

	public void setMinX(int minX)
	{
		this.minX = minX;
	}

	public int getMaxX()
	{
		return maxX;
	}

	public void setMaxX(int maxX)
	{
		this.maxX = maxX;
	}

	public int getMinY()
	{
		return minY;
	}

	public void setMinY(int minY)
	{
		this.minY = minY;
	}

	public int getMaxY()
	{
		return maxY;
	}

	public void setMaxY(int maxY)
	{
		this.maxY = maxY;
	}

	public int getXlength()
	{
		return xlength;
	}

	public void setXlength(int xlength)
	{
		this.xlength = xlength;
	}

	public int getYlength()
	{
		return ylength;
	}

	public void setYlength(int ylength)
	{
		this.ylength = ylength;
	}

	public void expand(Rectangle bounds)
	{
		minX = (int) Math.min(minX, bounds.getMinX() - 1);
		minY = (int) Math.min(minY, bounds.getMinY() - 1);
		maxX = (int) Math.max(maxX, bounds.getMaxX() + 1);
		maxY = (int) Math.max(maxY, bounds.getMaxY() + 1);
		/*
		 * Prevent zero length bounds
		 */
		if (maxX - minX <= 0)
		{
			minX = 0;
			maxX = 1;
		}
		if (maxY - minY <= 0)
		{
			minY = 0;
			maxY = 1;
		}
		this.xlength = this.maxX - this.minX;
		this.ylength = this.maxY - this.minY;
	}

	public void addWall(int entityId)
	{
		this.walls.add(entityId);
	}

	public void addRoom(int entityId)
	{
		this.rooms.add(entityId);
	}

	public void addDoor(int entityId)
	{
		this.doors.add(entityId);
	}

	public void addDynamicEntity(int entityId)
	{
		this.dynamicEntities.add(entityId);
	}

	public void addFurniture(int entityId)
	{
		this.furniture.add(entityId);
	}

	public void removeWall(int entityId)
	{
		this.walls.removeValue(entityId);
	}

	public void removeFurniture(int entityId)
	{
		this.furniture.removeValue(entityId);
	}

	public void removeDynamicEntity(int entityId)
	{
		this.dynamicEntities.removeValue(entityId);
	}

	public void removeRoom(int entityId)
	{
		this.rooms.removeValue(entityId);
	}

	public IntBag getWalls()
	{
		return walls;
	}

	public IntBag getRooms()
	{
		System.out.println("Returning rooms: "+rooms);
		return rooms;
	}

	public IntBag getDoors()
	{
		return doors;
	}

	public IntBag getDynamicEntities()
	{
		return dynamicEntities;
	}

	public IntBag getFurniture()
	{
		return furniture;
	}
}

package com.massisframework.massis.ecs.components;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

import straightedge.geom.KPoint;

public class BuildingLocation extends Component {

	/**
	 * Reference to the current floor
	 * 
	 * @treatAsPrivate
	 */
	@EntityId
	public int floorId;
	/**
	 * @treatAsPrivate
	 */
	public KPoint point = new KPoint();
	/**
	 * @treatAsPrivate
	 */
	public float y = 0;

	public void set(KPoint point)
	{
		this.setX(point.x);
		this.setZ(point.y);
		this.setY(0);
	}

	public BuildingLocation setX(double x)
	{
		this.point.x = x;
		return this;
	}

	private void setY(double y)
	{
		this.y = (float) y;
	}

	public BuildingLocation setZ(double z)
	{
		this.point.y = z;
		return this;
	}

	public float getX()
	{
		return (float) point.x;
	}

	public float getZ()
	{
		return (float) point.y;
	}

	public void setFloor(int floorId)
	{
		this.floorId = floorId;
	}

	public int getFloorId()
	{
		return this.floorId;
	}

}

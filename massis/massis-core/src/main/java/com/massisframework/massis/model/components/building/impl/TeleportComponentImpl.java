package com.massisframework.massis.model.components.building.impl;

import java.util.Map;

import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.components.TeleportComponent;
import com.massisframework.massis.model.components.building.Coordinate2DComponent;
import com.massisframework.massis.model.components.building.FloorContainmentComponent;
import com.massisframework.massis.sim.SimulationEntity;
import com.massisframework.massis.util.geom.CoordinateHolder;

public class TeleportComponentImpl implements TeleportComponent {

	private Floor targetFloor;
	private CoordinateHolder targetLocation;
	private TeleportType teleportType;
	private String targetTeleportName;
	private Map<String, SimulationEntity> teleportMap;

	public TeleportComponentImpl(Map<String, SimulationEntity> teleportMap)
	{
		this.teleportMap = teleportMap;
	}

	@Override
	public Floor getTargetFloor()
	{
		if (this.targetFloor == null)
		{
			buildConnection();
		}
		return targetFloor;
	}

	private void buildConnection()
	{
		SimulationEntity target = this.teleportMap.get(this.targetTeleportName);
		this.targetLocation = target.get(Coordinate2DComponent.class);
		this.targetFloor = target.get(FloorContainmentComponent.class)
				.getFloor();
	}

	public void setTargetFloor(Floor targetFloor)
	{
		this.targetFloor = targetFloor;
	}

	@Override
	public CoordinateHolder getTargetLocation()
	{
		if (this.targetLocation == null)
		{
			buildConnection();
		}
		return targetLocation;
	}

	public void setTargetLocation(CoordinateHolder targetLocation)
	{
		this.targetLocation = targetLocation;
	}

	@Override
	public TeleportType getTeleportType()
	{
		return teleportType;
	}

	public void setTeleportType(TeleportType teleportType)
	{
		this.teleportType = teleportType;
	}

	public String getTargetTeleportName()
	{
		return targetTeleportName;
	}

	public void setTargetTeleportName(String targetTeleportName)
	{
		this.targetTeleportName = targetTeleportName;
	}

}

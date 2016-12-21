package com.massisframework.massis.model.components.building.impl;

import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.components.TeleportComponent;
import com.massisframework.massis.sim.SimulationEntity;

public class TeleportComponentImpl implements TeleportComponent {

	private TeleportType teleportType;
	private SimulationEntity target;
	private String name;

	public TeleportComponentImpl(String name, TeleportType teleportType)
	{
		this.name = name;
		this.teleportType = teleportType;
	}

	@Override
	public TeleportType getTeleportType()
	{
		return teleportType;
	}

	@Override
	public SimulationEntity getTarget()
	{
		return this.target;
	}

	public void setTarget(SimulationEntity target)
	{
		this.target = target;
	}

	@Override
	public String getTeleportName()
	{
		return name;
	}

	@Override
	public int getDistanceToFloor(Floor other)
	{
		// TODO Default return value
		return 10;
	}

}

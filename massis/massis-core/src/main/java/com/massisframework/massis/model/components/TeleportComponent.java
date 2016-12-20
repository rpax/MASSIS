package com.massisframework.massis.model.components;

import straightedge.geom.KPoint;

public interface TeleportComponent extends SimulationComponent{

	public long getTargetFloorId();
	public <T extends KPoint> T getTargetLocation(T store);
}

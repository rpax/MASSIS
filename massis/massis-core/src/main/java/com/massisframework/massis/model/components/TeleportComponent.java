package com.massisframework.massis.model.components;

import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.util.geom.CoordinateHolder;

public interface TeleportComponent extends SimulationComponent {

	public static enum TeleportType {
		START, END
	}
	public Floor getTargetFloor();

	public CoordinateHolder getTargetLocation();

	TeleportType getTeleportType();
}

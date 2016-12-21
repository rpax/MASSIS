package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.SimulationEntity;

public interface TeleportComponent extends SimulationComponent {

	public static enum TeleportType {
		START, END
	}
	public SimulationEntity getTarget();

	TeleportType getTeleportType();
	
	public String getTeleportName();
}

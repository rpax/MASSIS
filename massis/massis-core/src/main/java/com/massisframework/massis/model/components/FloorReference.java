package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.SimulationComponent;

public interface FloorReference extends SimulationComponent {

	public void setFloorId(int fId);

	public int getFloorId();
}

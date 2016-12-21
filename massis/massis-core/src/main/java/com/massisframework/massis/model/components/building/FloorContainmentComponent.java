package com.massisframework.massis.model.components.building;

import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.components.SimulationComponent;

public interface FloorContainmentComponent extends SimulationComponent {

	public Floor getFloor();

	public void setFloor(Floor f);
}

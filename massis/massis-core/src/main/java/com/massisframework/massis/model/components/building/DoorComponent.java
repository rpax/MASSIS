package com.massisframework.massis.model.components.building;

import com.massisframework.massis.model.components.SimulationComponent;

public interface DoorComponent extends SimulationComponent{

	boolean isOpened();

	void setOpen(boolean open);
}

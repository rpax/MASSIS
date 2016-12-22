package com.massisframework.massis.model.components.building.impl;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.building.DoorComponent;

public class DoorComponentImpl implements DoorComponent {
	@Inject
	private DoorComponentImpl()
	{
	}
	@Override
	public boolean isOpened()
	{
		return true;
	}


	@Override
	public void setOpen(boolean open)
	{
		
	}

}

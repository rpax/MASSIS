package com.massisframework.massis.displays.floormap.layers;

import com.massisframework.gui.DrawableZone;
import com.massisframework.massis.model.building.IFloor;

public class DrawableFloor implements DrawableZone{

	private IFloor floor;

	public DrawableFloor(IFloor f) {
		this.floor=f;
	}
	
	@Override
	public float getMaxX() {
		return this.floor.getMaxX();
	}

	@Override
	public float getMaxY() {
		return this.floor.getMaxY();
	}

	@Override
	public float getMinX() {
		return this.floor.getMinX();
	}

	@Override
	public float getMinY() {
		return this.floor.getMinY();
	}

	@Override
	public String getName() {
		return this.floor.getName();
	}

	public IFloor getFloor() {
		return floor;
	}


}

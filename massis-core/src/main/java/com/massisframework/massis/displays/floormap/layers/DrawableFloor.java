package com.massisframework.massis.displays.floormap.layers;

import com.massisframework.gui.DrawableZone;
import com.massisframework.massis.model.building.Floor;

public class DrawableFloor implements DrawableZone{

	private Floor floor;

	public DrawableFloor(Floor f) {
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

	public Floor getFloor() {
		return floor;
	}


}

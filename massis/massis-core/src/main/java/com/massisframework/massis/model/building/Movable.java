package com.massisframework.massis.model.building;

import com.massisframework.massis.model.components.Location;
/**
 * Element capable of changing its location
 * @author rpax
 *
 */
public interface Movable {
	public void moveTo(Location other);
	public void moveTo(double x,double y,Floor f);
}

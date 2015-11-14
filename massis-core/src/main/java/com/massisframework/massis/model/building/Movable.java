package com.massisframework.massis.model.building;

import com.massisframework.massis.model.location.Location;
/**
 * Element capable of changing its location
 * @author rpax
 *
 */
public interface Movable {
	public void moveTo(Location other);
}

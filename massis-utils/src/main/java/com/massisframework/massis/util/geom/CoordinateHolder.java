package com.massisframework.massis.util.geom;

import straightedge.geom.KPoint;

public interface CoordinateHolder {

	/**
	 * 
	 * @return the x coordinate of this element
	 */
	public double getX();

	/**
	 * 
	 * @return the y coordinate of this object
	 */
	public double getY();

	/**
	 * 
	 * @return the point of this object.<strong>Caution:</strong> it is
	 *         <strong>not</strong> given as a copy.
	 */
	public KPoint getXY();

}

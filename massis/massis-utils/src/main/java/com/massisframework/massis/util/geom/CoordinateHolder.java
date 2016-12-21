package com.massisframework.massis.util.geom;

import java.util.Objects;

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
	 * Returns the coordinates of this object.
	 *
	 * @param coord
	 *            available 1D lenght 2 array
	 * @return the same array, filled with the coordinates of this object
	 */
	default double[] getXYCoordinates(double[] coord)
	{
		Objects.requireNonNull(coord);
		coord[0] = this.getX();
		coord[1] = this.getY();
		return coord;
	}

	default <K extends KPoint> K getXYCoordinates(K store)
	{
		store.setX(this.getX());
		store.setY(this.getY());
		return store;
	}

}

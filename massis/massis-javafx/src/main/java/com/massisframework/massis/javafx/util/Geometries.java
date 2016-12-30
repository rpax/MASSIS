package com.massisframework.massis.javafx.util;

import javafx.scene.transform.Transform;

public final class Geometries {

	private Geometries()
	{
	}

	/**
	 * Transforms the specified point by this transform. This method can be used
	 * only for 2D transforms.
	 * 
	 * @param x
	 *            the X coordinate of the point
	 * @param y
	 *            the Y coordinate of the point
	 * @return the X coordinate of the transformed point
	 */
	public static double transformX(Transform s, double x, double y)
	{
		return s.getMxx() * x + s.getMxy() * y + s.getTx();

	}

	/**
	 * Transforms the specified point by this transform. This method can be used
	 * only for 2D transforms.
	 * 
	 * @param x
	 *            the X coordinate of the point
	 * @param y
	 *            the Y coordinate of the point
	 * @return the Y coordinate of the transformed point
	 */

	public static double transformY(Transform s, double x, double y)
	{
		return s.getMyx() * x + s.getMyy() * y + s.getTy();
	}

}

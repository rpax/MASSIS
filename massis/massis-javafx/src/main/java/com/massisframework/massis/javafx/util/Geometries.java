package com.massisframework.massis.javafx.util;

import javafx.collections.ObservableList;
import javafx.scene.shape.Polygon;
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

	public static void createRegularPolygon(int numPoints,
			double distFromCenterToPoints, ObservableList<Double> store)
	{
		store.clear();
		if (numPoints < 3)
		{
			throw new IllegalArgumentException(
					"numPoints must be 3 or more, it can not be " + numPoints
							+ ".");
		}
		double angleIncrement = Math.PI * 2f / (numPoints);
		double radius = distFromCenterToPoints;
		double currentAngle = 0;
		for (int k = 0; k < numPoints; k++)
		{
			double x = radius * Math.cos(currentAngle);
			double y = radius * Math.sin(currentAngle);
			store.add(x);
			store.add(y);
			currentAngle += angleIncrement;
		}
	}

	public static Polygon createRegularPolygon(int numPoints,
			double distFromCenterToPoints, Polygon store)
	{
		createRegularPolygon(numPoints, distFromCenterToPoints,
				store.getPoints());
		return store;
	}

	public static Polygon createRegularPolygon(int numPoints,
			double distFromCenterToPoints)
	{

		return Geometries.createRegularPolygon(numPoints,
				distFromCenterToPoints,
				new Polygon());
	}
}

package com.massisframework.massis.util.geom;

import java.util.ArrayList;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

/**
 * Much credit due to: <a href="http://www.openprocessing.org/user/8371">Jacob
 * Haip's code</a>
 * 
 * @author rpax
 * 
 */
public class ContainmentPolygon  {
	public double x, y;
	public ArrayList<KVector> points;

	public ContainmentPolygon(double _x, double _y, ArrayList<KVector> p) {
		x = _x;
		y = _y;
		points = p;
	}

	public ContainmentPolygon(KPolygon polygon) {
		final KPoint center = polygon.getCenter();
		this.x = center.x;
		this.y = center.y;
		this.points = new ArrayList<>(polygon.getPoints().size());
		for (KPoint kp : polygon.getPoints())
		{
			this.points.add(new KVector(kp.x - center.x, kp.y - center.y));
		}
	}

}

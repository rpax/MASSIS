package com.massisframework.massis.util.geom;

import java.awt.geom.Point2D;

import straightedge.geom.KPoint;

/**
 * Based on PVector class of the processing Project (http://processing.org)
 * 
 * @author rpax
 * 
 */
public class KVector extends KPoint implements CoordinateHolder {

	public static final KVector ZERO = new KVector();

	public KVector(KPoint other) {
		this.x = other.x;
		this.y = other.y;
	}

	public KVector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public KVector() {
		this.x = 0;
		this.y = 0;
	}

	public static KVector sub(KPoint v1, KPoint v2) {
		return new KVector(v1.x - v2.x, v1.y - v2.y);
	}

	public KVector sub(KPoint v2) {
		x -= v2.x;
		y -= v2.y;
		return this;
	}

	public KVector normalize() {
		double m = magnitude();
		if (m != 0 && m != 1)
		{
			div(m);
		}
		return this;
	}

	public static KVector normalize(KVector k) {
		double m = k.magnitude();
		if (m != 0 && m != 1)
		{
			return div(k, m);
		}
		return new KVector(k);
	}

	public KVector div(double n) {
		x /= n;
		y /= n;
		return this;
	}

	public static KVector div(KVector v, double n) {
		return new KVector(v.x / n, v.y / n);

	}
	
	public double magnitude() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}

	public KVector mult(double n) {
		x *= n;
		y *= n;
		return this;
	}

	public static KVector mult(double n, KVector a) {
		return new KVector(a.x * n, a.y * n);
	}

	/**
	 * Limit the magnitude of this vector
	 * 
	 * @param d
	 *            the maximum length to limit this vector
	 */
	public KVector limit(double d) {
		if (this.magnitude() > d)
		{
			normalize();
			mult(d);
		}
		return this;
	}
	public static KVector limit(KVector v,double d) {
		v=new KVector(v);
		v.limit(d);
		return v;
	}
	public KVector copy() {
		return new KVector(x, y);
	}

	public KVector add(KPoint v) {
		x += v.x;
		y += v.y;
		return this;
	}
	public static  KVector add(KPoint... vectors) {
		KVector res=new KVector();
		for (KPoint v : vectors)
		{
			res.add(v);
		}
		return res;
	}

	/**
	 * Calculate the angle of rotation for this vector (only 2D vectors)
	 * 
	 * @return the angle of rotation
	 */
	public double heading2D() {
		double angle = Math.atan2(-y, x);
		return -1 * angle;
	}

	

	public static double map(double value, double leftMin, double leftMax,
			double rightMin, double rightMax) {

		double leftSpan = leftMax - leftMin;
		double rightSpan = rightMax - rightMin;
		double valueScaled = (value - leftMin) / (leftSpan);
		return rightMin + (valueScaled * rightSpan);
	}

	public void set(double x, double y) {
		this.x = x;
		this.y = y;

	}

	public Point2D.Double asPoint2D() {
		return new Point2D.Double(this.x, this.y);
	}

	/**
	 * Calculate the dot product with another vector
	 * 
	 * @return the dot product
	 */
	public double dot(KVector v) {
		return x * v.x + y * v.y;
	}

	public double dot(double x, double y, double z) {
		return this.x * x + this.y * y;
	}

	static public double dot(KVector v1, KVector v2) {
		return v1.x * v2.x + v1.y * v2.y;
	}
	static public double dot(double v1x,double v2x,double v1y,double v2y) {
		return v1x * v2x + v1y * v2y;
	}
	static public double dot(double[] v1,double[] v2) {
		return v1[0] * v2[0] + v1[1] * v2[1];
	}
	static public double dot(int[] v1,int[] v2) {
		return v1[0] * v2[0] + v1[1] * v2[1];
	}
	static public double dot(double[] v1,int[] v2) {
		return v1[0] * v2[0] + v1[1] * v2[1];
	}
	static public double dot(int[] v1,double[] v2) {
		return v1[0] * v2[0] + v1[1] * v2[1];
	}
	public static KVector normal(double x1, double y1, double x2, double y2) {
		double nx, ny;
		if (x1 == x2)
		{
			nx = Math.signum(y2 - y1);
			ny = 0;
		}
		else
		{
			double f = (y2 - y1) / (x2 - x1);
			nx = f * Math.signum(x2 - x1) / Math.sqrt(1 + f * f);
			ny = -1 * Math.signum(x2 - x1) / Math.sqrt(1 + f * f);
		}
		return new KVector(nx, ny);
	}

	public static KVector normal(KVector start, KVector end) {
		return normal(start.x, start.y, end.x, end.y);
	}
	public static boolean equals(KPoint p1,KPoint p2){
		if (p1.x == p2.x && p1.y == p2.y){
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	public static int hashCode(KPoint point) {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(point.x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(point.y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof KPoint))
			return false;
		KPoint other = (KPoint) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	@Override
	public KPoint getXY() {
		return this;
	}
	public static KVector getNormalPoint(KPoint p, KPoint a, KPoint b) {
		// Vector from a to p
		KVector ap = KVector.sub(p, a);
		// Vector from a to b
		KVector ab = KVector.sub(b, a);
		ab.normalize(); // Normalize the line
		// Project vector "diff" onto line by using the dot product
		ab.mult(ap.dot(ab));
		KVector normalPoint = KVector.add(a, ab);
		return normalPoint;
	}

	public static KVector getNormalPoint(KPoint p, KLine line) {
		return getNormalPoint(p, line.from, line.to);
	}
}

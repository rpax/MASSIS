package rpax.massis.util.geom;

import straightedge.geom.AABB;
import straightedge.geom.KPoint;
/**
 * Represents a Line between two points.
 * @author rpax
 *
 */
public class KLine {

	public KVector from;
	public KVector to;

	public KLine(KPoint from, KPoint to) {
		this.from = new KVector(from);
		this.to = new KVector(to);
	}
	public KLine(double x0,double y0,double x1,double y1) {
		this.from = new KVector(x0,y0);
		this.to = new KVector(x1,y1);
	}
	public KLine copy() {
		return new KLine(from, to);
	}

	public void invert() {
		KVector aux = this.to;
		this.to = this.from;
		this.from = aux;
	}

	public KVector normal() {
		double nx, ny;
		if (from.x == to.x)
		{
			nx = Math.signum(to.y - from.y);
			ny = 0;
		}
		else
		{
			double f = (to.y - from.y) / (to.x - from.x);
			nx = f * Math.signum(to.x - from.x) / Math.sqrt(1 + f * f);
			ny = -1 * Math.signum(to.x - from.x) / Math.sqrt(1 + f * f);
		}
		return new KVector(nx, ny);
	}

	public KVector getFrom() {
		return from;
	}

	public KVector getTo() {
		return to;
	}

	public KVector center() {
		double coordX = (from.x + from.y) / 2.0;
		double coordY = (to.x + to.y) / 2.0;
		return new KVector(coordX, coordY);
	}

	public double ptSegDist(KPoint p) {
		return KPoint.ptSegDist(from.x, from.y, to.x, to.y, p.x, p.y);
	}

	public double ptSegDistSq(KPoint p) {
		// return ptSegDistSq(start.x, start.y, end.x, end.y, x, y);
		return KPoint.ptSegDistSq(from.x, from.y, to.x, to.y, p.x, p.y);
	}

	public KPoint getClosestPointOnSegment(double px, double py) {
		return KPoint.getClosestPointOnSegment(this.from.x, this.from.y,
				this.to.x, this.to.y, px, py);
	}

	public double length() {
		return KPoint.distance(from, to);
	}

	public double lengthSq() {
		return from.distanceSq(to);
	}

	public boolean intersects(KLine other) {

		return doLineSegmentsIntersect(this,other);
	}

	public boolean containsPoint(KPoint p, double precision) {
		return KPoint.ptSegDist(this.from, this.to, p) < precision;
	}

	

	public KPoint getIntersectionPoint(KLine l2) {
		if (doLineSegmentsIntersect(this,l2))
			return KPoint.getLineLineIntersection(this.from, this.to, l2.from, l2.to);
		return null;
	}

	public boolean intersectsAABB(double rect_x, double rect_y, double w,
			double h) {
		return squareIntersects(rect_x, rect_y, w, h, this);
	}

	public boolean intersectsAABB(AABB aabb) {
		return squareIntersects(aabb.p.x, aabb.p.y, aabb.w(), aabb.h(), this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof KLine))
			return false;
		KLine other = (KLine) obj;
		if (from == null)
		{
			if (other.from != null)
				return false;
		}
		else if (!from.equals(other.from))
			return false;
		if (to == null)
		{
			if (other.to != null)
				return false;
		}
		else if (!to.equals(other.to))
			return false;
		return true;
	}

	// //////////////////////////
	// ///////////////////////////////
	private static final int INSIDE = 0; // 0000
	private static final int LEFT = 1; // 0001
	private static final int RIGHT = 2; // 0010
	private static final int BOTTOM = 4; // 0100
	private static final int TOP = 8; // 1000

	private static int computeOutCode(double rect_x, double rect_y, double w,
			double h, double x, double y) {
		int code;

		code = INSIDE; // initialised as being inside of clip window

		if (x < rect_x) // to the left of clip window
			code |= LEFT;
		else if (x > rect_x + w) // to the right of clip
									// window
			code |= RIGHT;
		if (y < rect_y) // below the clip window
			code |= BOTTOM;
		else if (y > rect_y + h) // above the clip window
			code |= TOP;

		return code;
	}

	// Cohen–Sutherland clipping algorithm clips a line from
	// P0 = (x0, y0) to P1 = (x1, y1) against a rectangle with
	// diagonal from (xmin, ymin) to (xmax, ymax).
	private static boolean squareIntersects(double rect_x, double rect_y,
			double w, double h, KLine line) {
		// compute outcodes for P0, P1, and whatever point lies outside the
		// clip rectangle
		double x0 = line.from.x;
		double y0 = line.from.y;
		double x1 = line.to.x;
		double y1 = line.to.y;
		int outcode0 = computeOutCode(rect_x, rect_y, w, h, x0, y0);
		int outcode1 = computeOutCode(rect_x, rect_y, w, h, x1, y1);
		final double ymax = (rect_y + h);
		final double xmax = (rect_x + w);
		final double ymin = (rect_y);
		final double xmin = (rect_x);
		boolean accept = false;
		while (true)
		{
			if (!((outcode0 | outcode1) != 0))
			{ // Bitwise OR is 0. Trivially accept and get out of loop
				accept = true;
				break;
			}
			else if ((outcode0 & outcode1) != 0)
			{ // Bitwise AND is not 0. Trivially reject and get out of loop
				break;
			}
			else
			{
				// failed both tests, so calculate the line segment to clip
				// from an outside point to an intersection with clip edge
				double x = 0, y = 0;

				// At least one endpoint is outside the clip rectangle; pick
				// it.
				int outcodeOut = (outcode0 != 0) ? outcode0 : outcode1;

				// Now find the intersection point;
				// use formulas y = y0 + slope * (x - x0), x = x0 + (1 /
				// slope) * (y - y0)
				if ((outcodeOut & TOP) != 0)
				{ // point is above the clip rectangle
					x = x0 + (x1 - x0) * (ymax - y0) / (y1 - y0);
					y = ymax;
				}
				else if ((outcodeOut & BOTTOM) != 0)
				{ // point is below the clip rectangle
					x = x0 + (x1 - x0) * (ymin - y0) / (y1 - y0);
					y = ymin;
				}
				else if ((outcodeOut & RIGHT) != 0)
				{ // point is to the right of clip rectangle
					y = y0 + (y1 - y0) * (xmax - x0) / (x1 - x0);
					x = xmax;
				}
				else if ((outcodeOut & LEFT) != 0)
				{ // point is to the left of clip rectangle
					y = y0 + (y1 - y0) * (xmin - x0) / (x1 - x0);
					x = xmin;
				}

				// Now we move outside point to intersection point to clip
				// and get ready for next pass.
				if (outcodeOut == outcode0)
				{
					x0 = x;
					y0 = y;
					outcode0 = computeOutCode(rect_x, rect_y, w, h, x0, y0);
				}
				else
				{
					x1 = x;
					y1 = y;
					outcode1 = computeOutCode(rect_x, rect_y, w, h, x1, y1);
				}
			}
		}
		return accept;
	}

	public AABB getAABB() {
		return new AABB(Math.min(this.from.x, this.to.x), Math.min(this.from.y,
				this.to.y), Math.max(this.from.x, this.to.x), Math.max(
				this.from.y, this.to.y));
	}
	public KPoint[] getAABBAsKPoints() {
		return new KPoint[]{
				new KPoint(Math.min(this.from.x, this.to.x), Math.min(this.from.y,this.to.y)),
				new KPoint(Math.max(this.from.x, this.to.x), Math.max(this.from.y, this.to.y))
				};
		
	}
	// =====================================================
	// Segment intersection
	// http://martin-thoma.com/how-to-check-if-two-line-segments-intersect/
	//
	/**
	 * <pre>
	 * +-----+  +-------------+      +----+
	 * |     |  | Do AABB     |  NO  |    |
	 * |START+--+ intersect?  +------+ NO |
	 * +-----+  +------+------+      +----+
	 *                 |  YES              
	 *          +------v------+            
	 *          | Does line   |            
	 *          | a intersects|            
	 *          | line segment|            
	 *          | b?          |            
	 *          +------+------+            
	 *                 |  YES              
	 * +---+    +------v------+            
	 * |   |YES | Does line   |            
	 * |YES+----+ b intersects|            
	 * |   |    | line segment|            
	 * |   |    | a?          |            
	 * +---+    +-------------+
	 * 
	 * </pre>
	 */
	//

	private static final double EPSILON = 0.000001;

	/**
	 * Calculate the cross product of two points.
	 * 
	 * @param a
	 *            first point
	 * @param b
	 *            second point
	 * @return the value of the cross product
	 */
	private static double crossProduct(KPoint a, KPoint b) {
		return a.x * b.y - b.x * a.y;
	}

	/**
	 * Check if bounding boxes do intersect. If one bounding box touches the
	 * other, they do intersect.
	 * 
	 * @param a
	 *            first bounding box
	 * @param b
	 *            second bounding box
	 * @return <code>true</code> if they intersect, <code>false</code>
	 *         otherwise.
	 */
	public static boolean doBoundingBoxesIntersect(KPoint[] a, KPoint[] b) {
		return a[0].x <= b[1].x && a[1].x >= b[0].x && a[0].y <= b[1].y
				&& a[1].y >= b[0].y;
	}

	/**
	 * Checks if a Point is on a line
	 * 
	 * @param a
	 *            line (interpreted as line, although given as line segment)
	 * @param b
	 *            point
	 * @return <code>true</code> if point is on line, otherwise
	 *         <code>false</code>
	 */
	public static boolean isPointOnLine(KLine a, KPoint b) {
		// Move the image, so that a.first is on (0|0)
		KLine aTmp = new KLine(new KPoint(0, 0), new KPoint(a.to.x - a.from.x,
				a.to.y - a.from.y));
		KPoint bTmp = new KPoint(b.x - a.from.x, b.y - a.from.y);
		double r = crossProduct(aTmp.to, bTmp);
		return Math.abs(r) < EPSILON;
	}

	/**
	 * Checks if a point is right of a line. If the point is on the line, it is
	 * not right of the line.
	 * 
	 * @param a
	 *            line segment interpreted as a line
	 * @param b
	 *            the point
	 * @return <code>true</code> if the point is right of the line,
	 *         <code>false</code> otherwise
	 */
	public static boolean isPointRightOfLine(KLine a, KPoint b) {
		// Move the image, so that a.first is on (0|0)
		KLine aTmp = new KLine(new KPoint(0, 0), new KPoint(a.to.x - a.from.x,
				a.to.y - a.from.y));
		KPoint bTmp = new KPoint(b.x - a.from.x, b.y - a.from.y);
		return crossProduct(aTmp.to, bTmp) < 0;
	}

	/**
	 * Check if line segment first touches or crosses the line that is defined
	 * by line segment second.
	 * 
	 * @param first
	 *            line segment interpreted as line
	 * @param second
	 *            line segment
	 * @return <code>true</code> if line segment first touches or crosses line
	 *         second, <code>false</code> otherwise.
	 */
	public static boolean lineSegmentTouchesOrCrossesLine(KLine a, KLine b) {
		return isPointOnLine(a, b.from)
				|| isPointOnLine(a, b.to)
				|| (isPointRightOfLine(a, b.from) ^ isPointRightOfLine(a, b.to));
	}

	/**
	 * Check if line segments intersect
	 * 
	 * @param a
	 *            first line segment
	 * @param b
	 *            second line segment
	 * @return <code>true</code> if lines do intersect, <code>false</code>
	 *         otherwise
	 */
	public static boolean doLineSegmentsIntersect(KLine a, KLine b) {
		KPoint[] box1 = a.getAABBAsKPoints();
		KPoint[] box2 = b.getAABBAsKPoints();
		return doBoundingBoxesIntersect(box1, box2)
				&& lineSegmentTouchesOrCrossesLine(a, b)
				&& lineSegmentTouchesOrCrossesLine(b, a);
	}
	
}

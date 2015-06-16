package rpax.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import rpax.massis.util.geom.KLine;
import straightedge.geom.AABB;
import straightedge.geom.KPoint;
/**
 * Utility class for helping the drawing of the layer
 * @author rpax
 *
 */
public class FloorMapLayersUtils {
	public static void drawThickLine(Graphics2D g, int x1, int y1, int x2,
			int y2, int thickness) {
		// The thick line is in fact a filled polygon

		int dX = x2 - x1;
		int dY = y2 - y1;
		// line length
		double lineLength = Math.sqrt(dX * dX + dY * dY);

		double scale = (thickness) / (2 * lineLength);

		// The x,y increments from an endpoint needed to create a rectangle...
		double ddx = -scale * dY;
		double ddy = scale * dX;
		ddx += (ddx > 0) ? 0.5 : -0.5;
		ddy += (ddy > 0) ? 0.5 : -0.5;
		int dx = (int) ddx;
		int dy = (int) ddy;

		// Now we can compute the corner points...
		int xPoints[] = new int[4];
		int yPoints[] = new int[4];

		xPoints[0] = x1 + dx;
		yPoints[0] = y1 + dy;
		xPoints[1] = x1 - dx;
		yPoints[1] = y1 - dy;
		xPoints[2] = x2 - dx;
		yPoints[2] = y2 - dy;
		xPoints[3] = x2 + dx;
		yPoints[3] = y2 + dy;

		g.fillPolygon(xPoints, yPoints, 4);
	}

	public static Color mixColors(Color color1, Color color2, double percent) {
		double inverse_percent = 1.0 - percent;
		int redPart = (int) (color1.getRed() * percent + color2.getRed()
				* inverse_percent);
		int greenPart = (int) (color1.getGreen() * percent + color2.getGreen()
				* inverse_percent);
		int bluePart = (int) (color1.getBlue() * percent + color2.getBlue()
				* inverse_percent);

		Color c = null;
		try
		{
			c = new Color(redPart, greenPart, bluePart);
		}
		catch (java.lang.IllegalArgumentException e)
		{
			System.err.println("percent=" + percent + ",redPart=" + redPart
					+ ",greenPart=" + greenPart + ",bluePart=" + bluePart);
			throw e;
		}
		return c;
	}
	public static int mixColorsAsInt( double percent,int color1, int color2) {
		double inverse_percent = 1.0 - percent;
		int redPart = (int) (((color1>> 16) & 0xFF) * percent + ((color2>> 16) & 0xFF)
				* inverse_percent);
		int greenPart = (int) (((color1>> 8) & 0xFF) * percent + ((color2>> 8) & 0xFF)
				* inverse_percent);
		int bluePart = (int) (((color1>> 0) & 0xFF) * percent + ((color2>> 0) & 0xFF)
				* inverse_percent);

		Color c = null;
		try
		{
			c = new Color(redPart, greenPart, bluePart);
		}
		catch (java.lang.IllegalArgumentException e)
		{
			System.err.println("percent=" + percent + ",redPart=" + redPart
					+ ",greenPart=" + greenPart + ",bluePart=" + bluePart);
			throw e;
		}
		return c.getRGB();
	}
	public static int mixColorsAsInt(double percent,int...colors) {
		int index=(int) (percent*(colors.length-2));
		return mixColorsAsInt(percent, colors[index+1],colors[index]);
		
	}
	public static void drawCircle(Graphics2D g, double centerX, double centerY,
			double radius) {
		g.drawOval((int) (centerX - radius), (int) (centerY - radius),
				(int) radius * 2, (int) radius * 2);
	}

	public static void drawCircle(Graphics2D g, KPoint center, double radius) {
		g.drawOval((int) (center.x - radius), (int) (center.y - radius),
				(int) radius * 2, (int) radius * 2);
	}
	public static void fillCircle(Graphics2D g, KPoint center, double radius) {
		g.fillOval((int) (center.x - radius), (int) (center.y - radius),
				(int) radius * 2, (int) radius * 2);
	}
	/**
	 * Draw an arrow line betwwen two point
	 * 
	 * @param g
	 *            the graphic component
	 * @param x1
	 *            x-position of first point
	 * @param y1
	 *            y-position of first point
	 * @param x2
	 *            x-position of second point
	 * @param y2
	 *            y-position of second point
	 * @param d
	 *            the width of the arrow
	 * @param h
	 *            the height of the arrow
	 *            http://stackoverflow.com/a/27461352/3315914
	 */
	public static void drawArrowLine(Graphics2D g, double x1, double y1,
			double x2, double y2, double d, double h) {
		int dx = (int) (x2 - x1), dy = (int) (y2 - y1);
		double D = Math.sqrt(dx * dx + dy * dy);
		double xm = D - d, xn = xm, ym = h, yn = -h, x;
		double sin = dy / D, cos = dx / D;

		x = xm * cos - ym * sin + x1;
		ym = xm * sin + ym * cos + y1;
		xm = x;

		x = xn * cos - yn * sin + x1;
		yn = xn * sin + yn * cos + y1;
		xn = x;

		int[] xpoints = { (int) x2, (int) xm, (int) xn };
		int[] ypoints = { (int) y2, (int) ym, (int) yn };

		g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
		g.fillPolygon(xpoints, ypoints, 3);
	}

	public static void drawArrowLine(Graphics2D g, KPoint p1, KPoint p2,
			double d, double h) {
		drawArrowLine(g, p1.x, p1.y, p2.x, p2.y, d, h);
	}
	public static void drawArrowLine(Graphics2D g, KLine line,
			double d, double h) {
		drawArrowLine(g, line.from,line.to, d, h);
	}
	public static void drawLine(Graphics2D g, KPoint p1, KPoint p2) {
		g.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
	}
	public static void drawLine(Graphics2D g, KLine line) {
		drawLine(g,line.from,line.to);
	}
	public static void drawAABB(Graphics2D g,AABB aabb) {
		drawRect(g,aabb.p.x, aabb.p.y, aabb.w(), aabb.h());
	}
	public static void drawRect(Graphics2D g,double x,double y, double width,double height) {
		g.drawRect((int)(x),(int)(y),(int)(width),(int)(height));
	}
	public static void drawRectAtCenter(Graphics2D g,double xCenter,double yCenter, double width,double height) {
		drawRect(g,xCenter-width/2,yCenter-height/2,width,height);
	}
	public static void drawRectAtCenter(Graphics2D g,KPoint center, double width,double height) {
		drawRectAtCenter(g,center.x,center.y,width,height);
	}
}

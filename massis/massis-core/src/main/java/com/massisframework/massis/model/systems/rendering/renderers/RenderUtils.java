package com.massisframework.massis.model.systems.rendering.renderers;

import com.massisframework.massis.model.components.ShapeComponent;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import straightedge.geom.KPolygon;

public class RenderUtils {

	public static void stroke(GraphicsContext gc, ShapeComponent sc,
			Paint paint)
	{
		stroke(gc, sc, paint, 1);
	}

	public static void stroke(GraphicsContext gc, ShapeComponent sc,
			Paint paint, double lineWidth)
	{
		gc.setStroke(paint);
		gc.setLineWidth(lineWidth);

		int nPoints = sc.getNumPoints();
		for (int i = 0; i < nPoints; i++)
		{
			double x1 = sc.getX(i);
			double y1 = sc.getY(i);

			double x2 = sc.getX((i + 1) % nPoints);
			double y2 = sc.getY((i + 1) % nPoints);

			gc.strokeLine(x1, y1, x2, y2);

		}
	}

	public static void stroke(GraphicsContext gc, KPolygon sc,
			Paint paint)
	{
		stroke(gc, sc, paint, 1);
	}

	public static void stroke(GraphicsContext gc, KPolygon sc,
			Paint paint, double lineWidth)
	{
		gc.setStroke(paint);
		gc.setLineWidth(lineWidth);

		int nPoints = sc.getPoints().size();
		for (int i = 0; i < nPoints; i++)
		{
			double x1 = sc.points.get(i).x;
			double y1 = sc.points.get(i).y;

			double x2 = sc.points.get((i + 1) % nPoints).x;
			double y2 = sc.points.get((i + 1) % nPoints).y;

			gc.strokeLine(x1, y1, x2, y2);

		}
	}

	private static ThreadLocal<double[][]> fillPoints_TL = ThreadLocal
			.withInitial(() -> new double[2][1024]);

	public static void fill(GraphicsContext gc, ShapeComponent sc, Paint paint)
	{
		gc.setFill(paint);
		double[][] points = fillPoints_TL.get();
		double[] xPoints = points[0];
		double[] yPoints = points[1];
		int nPoints = sc.getNumPoints();
		for (int i = 0; i < nPoints; i++)
		{
			xPoints[i] = sc.getX(i);
			yPoints[i] = sc.getY(i);
		}
		gc.fillPolygon(xPoints, yPoints, nPoints);
	}

	public static void fill(GraphicsContext gc, KPolygon sc, Paint paint)
	{
		gc.setFill(paint);
		double[][] points = fillPoints_TL.get();
		double[] xPoints = points[0];
		double[] yPoints = points[1];
		int nPoints = sc.getPoints().size();
		for (int i = 0; i < nPoints; i++)
		{
			xPoints[i] = sc.getPoint(i).x;
			yPoints[i] = sc.getPoint(i).y;
		}
		gc.fillPolygon(xPoints, yPoints, nPoints);
	}


}

package com.massisframework.massis.util.geom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.badlogic.gdx.math.EarClippingTriangulator;
import com.massisframework.massis.util.PathFindingUtils;
import com.seisw.util.geom.Poly;
import com.seisw.util.geom.PolyDefault;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;
import straightedge.geom.PolygonHolder;

public final class KPolygonUtils {

	/**
	 * {@link AffineTransform} que no hace nada.
	 */
	private static final AffineTransform IDENTITY_AFFINE_TRANSFORM = new AffineTransform();

	public static KPolygon intersection(Shape s1, Shape s2)
	{
		KPolygon k1 = createKPolygonFromShape(s1, true);
		KPolygon k2 = createKPolygonFromShape(s2, true);
		return intersection(k1, k2);
	}

	public static KPolygon intersection(KPolygon kpolygon1, KPolygon kpolygon2)
	{
		Poly m_Poly1 = new PolyDefault();
		Poly m_Poly2 = new PolyDefault();

		for (KPoint p : kpolygon1.getPoints())
		{
			m_Poly1.add(p.x, p.y);
		}
		for (KPoint p : kpolygon2.getPoints())
		{
			m_Poly2.add(p.x, p.y);
		}

		PolyDefault intersection = (PolyDefault) m_Poly1.intersection(m_Poly2);
		if (intersection.isEmpty())
		{
			return null;
		}
		ArrayList<KPoint> points = new ArrayList<>();
		for (int i = 0; i < intersection.getNumPoints(); i++)
		{
			points.add(new KPoint(intersection.getX(i), intersection.getY(i)));
		}
		return new KPolygon(points);
	}

	public static PolyDefault create(KPolygon kpoly)
	{
		PolyDefault poly = new PolyDefault();
		for (KPoint p : kpoly.getPoints())
		{
			poly.add(p.x, p.y);
		}
		return poly;
	}

	public static KPolygon createKPolygonFromShape(Shape s)
	{
		return createKPolygonFromShape(s, false);
	}

	/**
	 * Crea un poligono desde un {@link Shape}. Solo funciona con poligonos
	 * normales, sin agujeros. Shape debe estar hecho de rectas.
	 *
	 * @param s
	 *            La Shape correspondiente
	 * @return Un {@link KPolygon} cuya figura es como la de la Shape
	 *         proporcionada.
	 */
	public static KPolygon createKPolygonFromShape(Shape s,
			boolean avoidCloneIfPossible)
	{
		if (s instanceof KPolygon)
		{
			if (avoidCloneIfPossible)
			{
				return (KPolygon) s;
			} else
			{
				return new KPolygon((KPolygon) s);
			}
		}
		final double[] coords = new double[6];
		final PathIterator pathIterator = s
				.getPathIterator(IDENTITY_AFFINE_TRANSFORM);
		final ArrayList<KPoint> pointList = new ArrayList<KPoint>();

		while (!pathIterator.isDone())
		{
			pathIterator.currentSegment(coords);
			pointList.add(new KPoint(coords[0], coords[1]));
			pathIterator.next();
		}
		if (pointList.size() < 3)
		{
			return null;
		}
		KPolygon poly = new KPolygon(pointList);
		return poly;
	}

	public static KPolygon create(Poly poly)
	{
		ArrayList<KPoint> points = new ArrayList<>();
		for (int i = 0; i < poly.getNumPoints(); i++)
		{
			points.add(new KPoint(poly.getX(i), poly.getY(i)));
		}
		return new KPolygon(points);

	}

	public static boolean intersects(PolygonHolder p1, PolygonHolder p2)
	{
		return p1.getPolygon().intersectionPossible(p2.getPolygon())
				&& p1.getPolygon().intersects(p2.getPolygon());
	}

	public static ArrayList<KPolygon> union(List<? extends PolygonHolder> list)
	{
		// inicializacion
		ArrayList<KPolygon> res = new ArrayList<>();
		LinkedList<KPolygon> kpolygons = new LinkedList<KPolygon>();
		for (PolygonHolder ph : list)
		{
			kpolygons.add(ph.getPolygon());
		}
		// KPolygon currentKPoly = kpolygons.remove(0);
		// Poly currentPoly = create(currentKPoly);
		while (!kpolygons.isEmpty())
		{
			LinkedList<KPolygon> candidates = new LinkedList<>();
			Queue<KPolygon> queue = new LinkedList<KPolygon>();
			queue.add(kpolygons.remove(0));
			while (!queue.isEmpty())
			{
				KPolygon currentKPoly = queue.poll();
				Iterator<KPolygon> it = kpolygons.iterator();
				while (it.hasNext())
				{
					KPolygon next = it.next();
					if (intersects(next, currentKPoly))
					{
						queue.add(next);
						it.remove();
					}
				}
				candidates.add(currentKPoly);
			}
			// unimos los candidatos
			if (!candidates.isEmpty())
			{
				Poly current = create(candidates.remove(0));
				for (KPolygon candidate : candidates)
				{
					Poly union = current.union(create(candidate));
					if (union.getNumInnerPoly() > 1)
					{
						// no funciona. De vuelta a la lista ppal.
						kpolygons.add(candidate);
					} else
					{
						current = union;
					}
				}
				res.add(create(current));
			}

		}
		return res;
	}

	public static ArrayList<KLine> getLines(KPolygon poly)
	{
		ArrayList<KLine> lines = new ArrayList<>();

		ArrayList<KPoint> points = poly.getPoints();
		for (int i = 0; i < points.size() - 1; i++)
		{
			lines.add(new KLine(points.get(i), points.get(i + 1)));
		}
		lines.add(new KLine(points.get(points.size() - 1), points.get(0)));

		return lines;
	}

	public static KPoint[] getBoundaryPointsClosestTo(KPolygon poly, double x,
			double y, int npoints)
	{
		npoints = Math.min(npoints, poly.getPoints().size());
		// double closestDistanceSq = Double.MAX_VALUE;

		int[] closestIndex = new int[npoints];
		int[] closestNextIndex = new int[npoints];
		double[] closestDistanceSq = new double[npoints];
		Arrays.fill(closestIndex, -1);
		Arrays.fill(closestNextIndex, -1);
		Arrays.fill(closestDistanceSq, Double.MAX_VALUE);

		ArrayList<KPoint> points = poly.getPoints();
		int nextI;
		for (int i = 0; i < points.size(); i++)
		{
			nextI = (i + 1 == points.size() ? 0 : i + 1);
			KPoint p = points.get(i);
			KPoint pNext = points.get(nextI);
			double ptSegDistSq = KPoint.ptSegDistSq(p.x, p.y, pNext.x, pNext.y,
					x, y);
			for (int j = 0; j < closestDistanceSq.length; j++)
			{
				if (ptSegDistSq < closestDistanceSq[j])
				{
					shiftRight(closestDistanceSq, j);
					shiftRight(closestIndex, j);
					shiftRight(closestNextIndex, j);
					closestDistanceSq[j] = ptSegDistSq;
					closestIndex[j] = i;
					closestNextIndex[j] = nextI;
					break;
				}
			}

		}
		KPoint[] res = new KPoint[npoints];
		for (int i = 0; i < npoints; i++)
		{
			KPoint p = points.get(closestIndex[i]);
			KPoint pNext = points.get(closestNextIndex[i]);
			res[i] = KPoint.getClosestPointOnSegment(p.x, p.y, pNext.x, pNext.y,
					x, y);
		}
		return res;
	}

	private static void shiftRight(int[] array, int position)
	{
		for (int i = position + 1; i < array.length; i++)
		{
			array[i] = array[i - 1];
		}
	}

	private static void shiftRight(double[] array, int position)
	{

		for (int i = position + 1; i < array.length; i++)
		{
			array[i] = array[i - 1];
		}
	}

	public static KPolygon[] triangulate(KPolygon polygon)
	{

		ArrayList<KPoint> polyPoints = polygon.getPoints();
		EarClippingTriangulator ect = new EarClippingTriangulator();
		float[] points = new float[polyPoints.size() * 2];
		int index = 0;
		for (KPoint point : polyPoints)
		{
			points[index++] = (float) point.x;
			points[index++] = (float) point.y;
		}
		short[] triangles = ect.computeTriangles(points).toArray();
		KPolygon[] trianglePolys = new KPolygon[triangles.length / 3];
		int tIndex = 0;
		for (int i = 0; i < trianglePolys.length; i++)
		{
			final int p1_index = triangles[tIndex++];
			final int p2_index = triangles[tIndex++];
			final int p3_index = triangles[tIndex++];
			// ya tenemos los 3 indices
			final KPoint p1 = polyPoints.get(p1_index);
			final KPoint p2 = polyPoints.get(p2_index);
			final KPoint p3 = polyPoints.get(p3_index);
			trianglePolys[i] = new KPolygon(p1, p2, p3);

		}
		return trianglePolys;
	}

}

package rpax.massis.util.pathfinding.straightedge;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import rpax.massis.util.geom.KPolygonUtils;
import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;
import straightedge.geom.PolygonBufferer;
import straightedge.geom.PolygonConverter;
import straightedge.geom.path.PathBlockingObstacleImpl;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;

/**
 * Clase de utilidades de pathfinding,
 * 
 * @author rpax
 * 
 */
public final class PathFindingUtils {
	/**
	 * {@link AffineTransform} que no hace nada.
	 */
	private static final AffineTransform IDENTITY_AFFINE_TRANSFORM = new AffineTransform();
	/**
	 * {@link PolygonBufferer} cacheado, asi no hay que estar creando uno nuevo
	 * cada vez
	 */
	private static final PolygonBufferer POLYGON_BUFFERER = new PolygonBufferer();

	/**
	 * Crea un poligono desde un {@link Shape}. Solo funciona con poligonos
	 * normales, sin agujeros. Shape debe estar hecho de rectas.
	 * 
	 * @param s
	 *            La Shape correspondiente
	 * @return Un {@link KPolygon} cuya figura es como la de la Shape
	 *         proporcionada.
	 */
	public static KPolygon createKPolygonFromShape(Shape s) {
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
			return null;
		KPolygon poly = new KPolygon(pointList);
		return poly;
	}

	/**
	 * Expande un poligono
	 * 
	 * @param original
	 *            poligono original
	 * @param bufferAmount
	 *            cuanto se quiere expandir
	 * @return poligono expandido
	 */
	public static KPolygon buffer(KPolygon original, double bufferAmount) {
		return POLYGON_BUFFERER.buffer(original, bufferAmount, 1);
	}

	/**
	 * Dado un area, la divide en areas mas sencillas que la forman. Sirve para
	 * romper areas complejas y luego crear poligonos a partir de ellas.
	 * 
	 * @param area
	 *            el area
	 * @return una lista de sub-areas.
	 */
	public static List<Area> getAreas(Area area) {
		PathIterator iter = area.getPathIterator(null);
		List<Area> areas = new ArrayList<Area>();
		Path2D.Float poly = new Path2D.Float();

		while (!iter.isDone())
		{
			float point[] = new float[2]; // x,y
			int type = iter.currentSegment(point);
			if (type == PathIterator.SEG_MOVETO)
			{
				poly.moveTo(point[0], point[1]);
			}
			else if (type == PathIterator.SEG_CLOSE)
			{
				areas.add(new Area(poly));
				poly.reset();
			}
			else
			{
				poly.lineTo(point[0], point[1]);
			}
			iter.next();
		}
		return areas;
	}

	public static Geometry mergePolygons(List<KPolygon> polygons) {
		PolygonConverter polygonConverter = new PolygonConverter();
		List<Geometry> geometrys = new ArrayList<>();
		for (KPolygon poly : polygons)
		{
			geometrys.add(polygonConverter.makeJTSPolygonFrom(poly));
		}
		Geometry union = CascadedPolygonUnion.union(geometrys);
		return union;
	}

	public static ArrayList<KPolygon> getMinimizedPolygons2(List<KPolygon> obsts) {
		return KPolygonUtils.union(obsts);
	}

	

	public static PathBlockingObstacleImpl createObstacleFromInnerPolygon(
			KPolygon innerPolygon, double BUFFER_AMOUNT,
			int NUM_POINTS_IN_A_QUADRANT) {
		PolygonBufferer polygonBufferer = new PolygonBufferer();
		KPolygon outerPolygon = polygonBufferer.buffer(innerPolygon,
				BUFFER_AMOUNT, NUM_POINTS_IN_A_QUADRANT);
		if (outerPolygon == null)
		{
			// there was an error so return null;
			return null;
		}
		PathBlockingObstacleImpl pathBlockingObstacleImpl = new PathBlockingObstacleImpl(
				outerPolygon, innerPolygon);
		return pathBlockingObstacleImpl;
	}
}

package rpax.massis.util;

import java.util.ArrayList;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

import com.eteks.sweethome3d.model.Selectable;
/**
 * Utility class for transforming SH3D shapes into {@link KPolygon}
 * @author rpax
 *
 */
public class SH3DUtils {

	
	public static KPolygon createKPolygonFromSH3DObj(Selectable homePieceOfFurniture) {
		float[][] hpofPoints = homePieceOfFurniture.getPoints();
		// generamos el poligono a partir de los puntos del furniture
		ArrayList<KPoint> points = new ArrayList<>();
		for (float[] point : hpofPoints)
		{
			points.add(new KPoint(point[0], point[1]));
		}
		return new KPolygon(points);
	}
	
}

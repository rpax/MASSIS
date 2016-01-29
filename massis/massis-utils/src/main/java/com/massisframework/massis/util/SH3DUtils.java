package com.massisframework.massis.util;

import java.util.ArrayList;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

import com.eteks.sweethome3d.model.Level;
import com.eteks.sweethome3d.model.Selectable;
/**
 * Utility class for transforming SH3D shapes into {@link KPolygon}
 * @author rpax
 *
 */
public class SH3DUtils {

	
	public static KPolygon createKPolygonFromSH3DObj(Selectable homePieceOfFurniture) {
		final float[][] hpofPoints = homePieceOfFurniture.getPoints();
		// generamos el poligono a partir de los puntos del furniture
		final ArrayList<KPoint> points = new ArrayList<>();
		for (final float[] point : hpofPoints)
		{
			points.add(new KPoint(point[0], point[1]));
		}
		return new KPolygon(points);
	}
	
	public static String getLevelName(Level lvl) {
		return lvl == null ? "NONAME" : lvl.getName();
	}
	public static float getLevelElevation(Level lvl) {
		return lvl == null ? 0f : lvl.getElevation();
	}
	
}

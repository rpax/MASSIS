package rpax.massis.sh3d.plugins.design;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import rpax.massis.sh3d.plugins.metadata.MASSISHomeMetadataManager;

import com.eteks.sweethome3d.model.Elevatable;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Level;
import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.model.Wall;

public final class DesignTools {
	private static final int LEFT_SIDE = 0;
	private static final int RIGHT_SIDE = 1;
 
	/**
	 * Valor arbitrario de pared invisible, lo suficientemente bajo como para
	 * que no se vea y con una precision que haga que sea practicamente
	 * imposible de reproducir "a mano".
	 */
	private static final float INVISIBLE_WALL_HEIGHT = Float.parseFloat("0."
			+ "Pilu".hashCode());

	public static void makeOuterWallsInvisible(Home home) {
		makeOuterWallsInvisible(home, emptyDesignerLog());
	}

	public static void makeOuterWallsInvisible(Home home, DesignerLog log) {

		final Collection<Wall> walls = home.getWalls();
		final Collection<Room> rooms = home.getRooms();
		ExecutorService executor = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());

		final HashMap<Room, Area> roomAreas = new HashMap<Room, Area>();
		for (Room room : rooms)
		{
			roomAreas.put(room, new Area(getShape(room)));
		}
		// lado izq
		for (final Wall wall : walls)
		{
			executor.submit(new Runnable() {

				public void run() {
					float[][] wallPoints = wall.getPoints();
					boolean intersectsLeft = false;
					boolean intersectsRight = false;
					for (Room room : rooms)
					{
						if (wall.getLevel() == room.getLevel())
						{
							Area roomArea = roomAreas.get(room);
							if (!intersectsLeft
									&& isRoomItersectingWallSide(wallPoints,
											LEFT_SIDE, roomArea))
							{
								intersectsLeft = true;

							}
							if (!intersectsRight
									&& isRoomItersectingWallSide(wallPoints,
											RIGHT_SIDE, roomArea))
							{
								intersectsRight = true;

							}
						}
						if (intersectsRight && intersectsLeft)
						{
							break;
						}
					}

					if (!intersectsLeft || !intersectsRight)
					{
						wall.setHeight(INVISIBLE_WALL_HEIGHT);

					}

				}
			});

		}
		executor.shutdown();

		try
		{
			executor.awaitTermination(1, TimeUnit.DAYS);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		home.setModified(true);
	}

	private static boolean isRoomItersectingWallSide(float[][] wallPoints,
			int wallSide, Area roomArea) {
		BasicStroke lineStroke = new BasicStroke(2);
		Shape wallSideShape = getWallSideShape(wallPoints, wallSide);
		Area wallSideTestArea = new Area(
				lineStroke.createStrokedShape(wallSideShape));
		float wallSideTestAreaSurface = getSurface(wallSideTestArea);
		wallSideTestArea.intersect(roomArea);
		if (!wallSideTestArea.isEmpty())
		{
			float wallSideIntersectionSurface = getSurface(wallSideTestArea);
			// Take into account only walls that shares a minimum surface with
			// the room
			if (wallSideIntersectionSurface > wallSideTestAreaSurface * 0.02f)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the surface of the given <code>area</code>.
	 */
	private static float getSurface(Area area) {
		// Add the surface of the different polygons of this room
		float surface = 0;
		List<float[]> currentPathPoints = new ArrayList<float[]>();
		for (PathIterator it = area.getPathIterator(null); !it.isDone();)
		{
			float[] roomPoint = new float[2];
			switch (it.currentSegment(roomPoint)) {
			case PathIterator.SEG_MOVETO:
				currentPathPoints.add(roomPoint);
				break;
			case PathIterator.SEG_LINETO:
				currentPathPoints.add(roomPoint);
				break;
			case PathIterator.SEG_CLOSE:
				float[][] pathPoints = currentPathPoints
						.toArray(new float[currentPathPoints.size()][]);
				surface += Math.abs(getSignedSurface(pathPoints));
				currentPathPoints.clear();
				break;
			}
			it.next();
		}
		return surface;
	}

	private static float getSignedSurface(float areaPoints[][]) {
		// From "Area of a General Polygon" algorithm described in
		// http://www.davidchandler.com/AreaOfAGeneralPolygon.pdf
		float area = 0;
		for (int i = 1; i < areaPoints.length; i++)
		{
			area += areaPoints[i][0] * areaPoints[i - 1][1];
			area -= areaPoints[i][1] * areaPoints[i - 1][0];
		}
		area += areaPoints[0][0] * areaPoints[areaPoints.length - 1][1];
		area -= areaPoints[0][1] * areaPoints[areaPoints.length - 1][0];
		return area / 2;
	}

	private static GeneralPath getPath(float[][] points, boolean closedPath) {
		GeneralPath path = new GeneralPath();
		path.moveTo(points[0][0], points[0][1]);
		for (int i = 1; i < points.length; i++)
		{
			path.lineTo(points[i][0], points[i][1]);
		}
		if (closedPath)
		{
			path.closePath();
		}
		return path;
	}

	private static Shape getWallSideShape(float[][] wallPoints, int wallSide) {
		if (wallPoints.length == 4)
		{
			if (wallSide == LEFT_SIDE)
			{
				return new Line2D.Float(wallPoints[0][0], wallPoints[0][1],
						wallPoints[1][0], wallPoints[1][1]);
			}
			else
			{
				return new Line2D.Float(wallPoints[2][0], wallPoints[2][1],
						wallPoints[3][0], wallPoints[3][1]);
			}
		}
		else
		{
			float[][] wallSidePoints = new float[wallPoints.length / 2][];
			System.arraycopy(wallPoints, wallSide == LEFT_SIDE ? 0
					: wallSidePoints.length, wallSidePoints, 0,
					wallSidePoints.length);
			return getPath(wallSidePoints, false);
		}
	}

	public static int[/* minX,minY,maxX,maxY */] getWallBounds(
			Collection<Wall> walls) {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (Wall w : walls)
		{
			float[][] points = w.getPoints();
			for (int i = 0; i < points.length; i++)
			{
				minX = (int) Math.min(minX, Math.floor(points[i][0]));
				minY = (int) Math.min(minY, Math.floor(points[i][1]));
				maxX = (int) Math.max(maxX, Math.ceil(points[i][0]));
				maxY = (int) Math.max(maxY, Math.ceil(points[i][1]));
			}
		}
		return new int[] { minX, minY, maxX, maxY };
	}

	public static void createIDs(Home home, DesignerLog log) {
		System.out.println("Nothing");
	}

	public static void showHomeIDData(Home home, DesignerLog log) {
		MASSISHomeMetadataManager manager = MASSISHomeMetadataManager
				.getHomeMetaData(home);
		log.clear();
		log.appendLine("SIZE: " + manager.size());
		log.appendLine("--------------------------");
		ArrayList<Serializable> ret = new ArrayList<Serializable>();
		ret.addAll(home.getWalls());
		ret.addAll(home.getFurniture());
		ret.addAll(home.getRooms());
		for (Serializable serializable : ret)
		{
			Map<String, String> metadata = manager.getMetadata(serializable);
			if (serializable instanceof HomePieceOfFurniture)
			{
				log.appendLine(serializable + ": " + metadata + " ("
						+ ((HomePieceOfFurniture) serializable).getName());
			}
			else
			{
				log.appendLine(serializable + ": " + metadata + "");
			}
		}

	}

	public static class LevelWallsMap {
		HashMap<Level, ArrayList<Wall>> map = new HashMap<Level, ArrayList<Wall>>();

		public void add(Wall wall) {
			Level lvl = wall.getLevel();
			ArrayList<Wall> wallsLvl = map.get(lvl);
			if (wallsLvl == null)
			{
				wallsLvl = new ArrayList<Wall>();
			}
			wallsLvl.add(wall);
			map.put(lvl, wallsLvl);
		}

		public ArrayList<Wall> getWalls(Level lvl) {
			ArrayList<Wall> walls = this.map.get(lvl);
			if (walls == null)
			{
				walls = new ArrayList<Wall>();
				this.map.put(lvl, walls);
			}
			return walls;
		}

		public Set<Level> keySet() {
			return this.map.keySet();
		}

		public Set<Entry<Level, ArrayList<Wall>>> entrySet() {
			return this.map.entrySet();
		}

		public Collection<ArrayList<Wall>> values() {
			return this.map.values();
		}

	}

	
	public static Shape getShape(Elevatable obj) {
		return callMethod(obj, "getShape", Shape.class);
	}

	@SuppressWarnings("unchecked")
	private static <T> T callMethod(Object object, String methodName,
			Class<T> returnType) {
		Method method;

		try
		{
			method = object.getClass().getDeclaredMethod(methodName);
			method.setAccessible(true);
			return (T) method.invoke(object);
		}
		catch (NoSuchMethodException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private static DesignerLog emptyDesignerLog;

	public static DesignerLog emptyDesignerLog() {
		if (emptyDesignerLog == null)
		{
			emptyDesignerLog = new DesignerLog() {

				public DesignerLog clear() {
					return emptyDesignerLog;
				}

				public DesignerLog appendLine(String str) {
					return emptyDesignerLog;
				}

				public DesignerLog append(String str) {
					return emptyDesignerLog;
				}
			};

		}
		return emptyDesignerLog;
	}
}

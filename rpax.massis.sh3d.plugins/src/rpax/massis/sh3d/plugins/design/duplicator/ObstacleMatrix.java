package rpax.massis.sh3d.plugins.design.duplicator;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import rpax.massis.sh3d.plugins.design.DesignTools;

import com.eteks.sweethome3d.model.Elevatable;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Level;
import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.model.Selectable;
import com.eteks.sweethome3d.model.Wall;
import com.eteks.sweethome3d.viewcontroller.HomeController;

public class ObstacleMatrix {
 
	private static final int MINX = 0, MINY = 1, MAXX = 2, MAXY = 3;
	private final int[] grid;
	private final int[] zs;
	private final int[] x_lengths;
	private final int[] y_lengths;

	public ObstacleMatrix(Home home,
			HomeController hc, int noDuplications) {
//		CatalogPieceOfFurniture f2 = hc.getFurnitureCatalogController().getSelectedFurniture().get(0);
//		HomePieceOfFurniture f2 = home.getFurniture().get(home.getFurniture().size() - 1);
//		Shape s = DesignTools.getShape(f2);
//		hc.addHomeFurniture();
//		hc.
		HomePieceOfFurniture item =  (HomePieceOfFurniture) home.getSelectedItems().get(0);
		Shape s = DesignTools.getShape(item);
		int cellSize = Math.max(s.getBounds().height, s.getBounds().width)*2;
		HashMap<Level, int[]> levelBounds = new HashMap<Level, int[]>();
		int max_level_id = 0;
		HashMap<Integer, Level> idLevelsMap = new HashMap<Integer, Level>();
		HashMap<Level, Integer> levelIdMap = new HashMap<Level, Integer>();

		for (Room r : home.getRooms())
			max_level_id=fillMaps(r, idLevelsMap, levelIdMap, levelBounds, max_level_id);
		for (Wall w : home.getWalls())
			max_level_id=fillMaps(w, idLevelsMap, levelIdMap, levelBounds, max_level_id);
		for (HomePieceOfFurniture f : home.getFurniture())
			max_level_id=fillMaps(f, idLevelsMap, levelIdMap, levelBounds, max_level_id);

		int arraySize = 0;
		int nlevels = levelBounds.size();
		System.out.println("#levels : "+nlevels+","+"max_level_id "+max_level_id);
		x_lengths = new int[nlevels];
		y_lengths = new int[nlevels];
		for (Entry<Level, int[]> entry : levelBounds.entrySet())
		{
			int[] bounds = entry.getValue();
			int z = levelIdMap.get(entry.getKey());
			 bounds[MINX] += cellSize;
			 bounds[MINY] += cellSize;
			 bounds[MAXX] -= cellSize;
			 bounds[MAXY] -= cellSize;
			x_lengths[z] = Math.max(((bounds[MAXX] - bounds[MINX]) / cellSize),1);
			y_lengths[z] = Math.max(((bounds[MAXY] - bounds[MINY]) / cellSize),1);
			arraySize += x_lengths[z] * y_lengths[z];
		}
		// inicializamos el array como toca.
		grid = new int[arraySize];
		zs=new int[arraySize];
		{
			int i = 0;
			for (int z = 0; z < nlevels; z++)
			{
				
				for (int x = 0; x < x_lengths[z]; x++)
				{
					for (int y = 0; y < y_lengths[z]; y++)
					{
						grid[i] =i;
						zs[i]=z;
						i++;
					}
				}
			}
		}
		ArrayList<Elevatable> furnitureAndWalls = new ArrayList<Elevatable>();
		furnitureAndWalls.addAll(home.getWalls());
		furnitureAndWalls.addAll(home.getFurniture());
		// Initialization #1 done.
		// Ahora lo que hay que hacer es coger las longitudes de los bounds, /
		// cellSize
		for (Elevatable elevatable : furnitureAndWalls)
		{
			Shape shape = DesignTools.getShape(elevatable);
			int z = levelIdMap.get(elevatable.getLevel());
			int minX = (int) (shape.getBounds().getMinX() / cellSize);
			int minY = (int) (shape.getBounds().getMinY() / cellSize);
			int maxX = (int) (shape.getBounds().getMaxX() / cellSize);
			int maxY = (int) (shape.getBounds().getMaxY() / cellSize);
			int realX, realY;
			for (int x = minX; x < maxX; x++)
			{
				realX = x * cellSize + levelBounds.get(elevatable.getLevel())[MINX];
				for (int y = minY; y < maxY; y++)
				{
					realY = y * cellSize + levelBounds.get(elevatable.getLevel())[MINY];
					if (shape.contains(realX, realY))
					{
						setObstacle_grid(x, y, z);
					}
				}
			}
		}
		shuffle();
		

		
		
		int d = 0;
	//	ArrayList<HomePieceOfFurniture> elems = new ArrayList<HomePieceOfFurniture>();
		

		for (int i = 0; i < grid.length && d < noDuplications; i++)
		{
			if (!isObstacle(i))
			{
				
				HomePieceOfFurniture copy = item.clone();
				int z=getZ_grid(i);
				Level lvl=idLevelsMap.get(z);
				int minX=levelBounds.get(lvl)[MINX];
				int minY=levelBounds.get(lvl)[MINY];
				home.addPieceOfFurniture(copy);
				copy.setX(getX_grid(i)*cellSize + minX);
				copy.setY(getY_grid(i)*cellSize + minY);
				copy.setLevel(lvl);
				
				d++;
				//setObstacle(i);
			}
		}
		

		// Ya tenemos el array.

	}
	@SuppressWarnings("unused")
	private static <T> List<T> asList(T t) {
		ArrayList<T> list = new ArrayList<T>();
		list.add(t);
		return list;
	}

	@SuppressWarnings("unused")
	private static <T> List<T> asList(T t, List<T> reusable) {
		reusable.clear();
		reusable.add(t);
		return reusable;
	}

	private void shuffle() {
		int[] arr=grid;
		int size = arr.length;
		Random rnd = ThreadLocalRandom.current();
		// Shuffle array
		for (int i = size; i > 1; i--)
		{
			int j = rnd.nextInt(i);
			int tmp = arr[i - 1];
			arr[i - 1] = arr[j];
			arr[j] = tmp;
			int tmp1 = zs[i - 1];
			zs[i - 1] = zs[j];
			zs[j] = tmp1;
		}

	}

	private static <T extends Selectable & Elevatable> int fillMaps(T r,
			HashMap<Integer, Level> idLevelsMap,
			HashMap<Level, Integer> levelIdMap,
			HashMap<Level, int[]> levelBounds, int max_level_id) {
		Level level = r.getLevel();
		if (!levelBounds.containsKey(level))
		{
			levelBounds.put(level, new int[] { Integer.MAX_VALUE,
					Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE });
			idLevelsMap.put(max_level_id, level);
			levelIdMap.put(level, max_level_id);
			max_level_id++;
		}
		int[] bounds = levelBounds.get(level);
		float[][] points = r.getPoints();
		for (int i = 0; i < points.length; i++)
		{
			bounds[MINX] = (int) Math.min(bounds[MINX], Math.floor(points[i][0]));
			bounds[MINY] = (int) Math.min(bounds[MINY], Math.floor(points[i][1]));
			bounds[MAXX] = (int) Math .max(bounds[MAXX], Math.ceil(points[i][0]));
			bounds[MAXY] = (int) Math .max(bounds[MAXY], Math.ceil(points[i][1]));
		}
		return max_level_id;
	}

	private final int getX_grid(int index) {
		return Math.abs(this.grid[index] ) / this.y_lengths[getZ_grid(index)];

	}

	private final int getY_grid(int index) {
		return (this.grid[index] ) % this.y_lengths[getZ_grid(index)];

	}

	private final int getZ_grid(int index) {
		return Math.abs(zs[index]);
	}

	private final boolean isObstacle(int index) {
		//return this.zs[index] < 0;
		return false;
	}

	private final void setObstacle(int index) {
		//ESTA MAL. No existe el -0
		this.zs[index]=-Math.abs(this.zs[index]);
	}

	private final int getIndex_grid(int x, int y, int z) {
		return x * y_lengths[z] + y;
	}

	
	private final void setObstacle_grid(int x, int y, int z) {
		this.setObstacle(getIndex_grid(x, y, z));
	}

}

/**
 *
 */
package com.massisframework.massis.model.components.building.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.inject.Inject;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.util.field.grid.quadtree.array.ArrayQuadTree;
import com.massisframework.massis.util.geom.ContainmentPolygon;

/**
 * Represents a level/Floor in the building
 *
 * @author rpax
 *
 */
public class FloorImpl implements Floor {

	
	// UID "generator"
	private static final AtomicInteger CURRENT_FLOOR_MAX_ID = new AtomicInteger(0);

	private static int getNewUID()
	{
		return CURRENT_FLOOR_MAX_ID.getAndIncrement();
	}

	/*
	 * Bounds
	 */
	

	// private final ArrayList<RoomConnector> roomConnectors = new
	// ArrayList<>();

	/**
	 * Polygons for using the containment behavior
	 */
	private List<ContainmentPolygon> containmentPolygons;
	

	private int minX, maxX, minY, maxY, xlength, ylength;
	/**
	 * QuadTree
	 */
	private ArrayQuadTree<?> quadPilu;

	
	private String floorName="floorName";
	/**
	 * Creates a floor with all the elements of a Level
	 *
	 * @param level3D
	 *            the level (1,2,3)
	 * @param rooms3D
	 *            the rooms in that level
	 * @param walls3D
	 *            the walls in that level
	 * @param furniture3D
	 *            the furniture in that level
	 */
	@Inject
	private FloorImpl()
	{
		
	}


//	/**
//	 * Creation of the containment polygons
//	 */
//	private void initializeContainmentPolygons()
//	{
//		this.containmentPolygons = new ArrayList<>();
//		for (final SimulationEntity w : this
//				.filterEntities(
//						ShapeComponent.class,
//						WallComponent.class))
//		{
//			// uh...
//			Shape shape = w.get(ShapeComponent.class).getShape();
//			KPolygon poly = PathFindingUtils.createKPolygonFromShape(shape);
//			this.containmentPolygons.add(new ContainmentPolygon(
//					new PolygonBufferer().buffer(poly, 50, 1)));
//		}
//
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.massisframework.massis.model.building.IFloor#getContainmentPolygons()
	 */
//	@Override
	public List<ContainmentPolygon> getContainmentPolygons()
	{
		return this.containmentPolygons;
	}

//	/**
//	 * Sets the maximum and minimum bounds.
//	 * 
//	 * TODO performance. Check only for new added items
//	 */
//	private void configureBounds()
//	{
//		double newminX = Integer.MAX_VALUE;
//		double newminY = Integer.MAX_VALUE;
//		double newmaxX = Integer.MIN_VALUE;
//		double newmaxY = Integer.MIN_VALUE;
//		for (final SimulationEntity r : this.filterEntities(ShapeComponent.class))
//		{
//			ShapeComponent s = r.get(ShapeComponent.class);
//			Rectangle2D bounds = s.getBounds();
//			newminX = (int) Math.min(newminX, bounds.getMinX());
//			newminY = (int) Math.min(newminY, bounds.getMinY());
//			newmaxX = (int) Math.max(newmaxX, bounds.getMaxX());
//			newmaxY = (int) Math.max(newmaxY, bounds.getMaxY());
//
//		}
//
//		newminX -= 1;
//		newminY -= 1;
//		newmaxX += 1;
//		newmaxY += 1;
//		/*
//		 * Prevent zero length bounds
//		 */
//		if (newmaxX - newminX <= 0)
//		{
//			newminX = 0;
//			newmaxX = 1;
//		}
//		if (newmaxY - newminY <= 0)
//		{
//			newminY = 0;
//			newmaxY = 1;
//		}
//
//		// TODO make more efficient
//		if (newminX < this.minX
//				|| newminY < this.minY
//				|| newmaxX > this.maxX
//				|| newmaxY > this.maxY)
//		{
//			this.minX = (int) Math.min(newminX, this.minX);
//			this.minY = (int) Math.min(newminY, this.minY);
//			this.maxX = (int) Math.max(newmaxX, this.maxX);
//			this.maxY = (int) Math.max(newmaxY, this.maxY);
//			this.xlength = this.maxX - this.minX;
//			this.ylength = this.maxY - this.minY;
//		}
//
//	}

//	/**
//	 * FIXME this is very inneficient
//	 * 
//	 * @deprecated
//	 * @param type
//	 * @return
//	 */
//	private <T extends SimulationComponent> Iterable<T> filterComponents(
//			Class<T> type)
//	{
//		return StreamSupport
//				.stream(this.entities.spliterator(), false)
//				.map(e -> e.get(type))
//				.filter(c -> c != null)
//				.map(type::cast)::iterator;
//	}
//
//	/**
//	 * FIXME this is very inneficient and obscure
//	 * 
//	 * @deprecated
//	 * @param type
//	 * @return
//	 */
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private Iterable<SimulationEntity> filterEntities(Class... types)
//	{
//		return this.streamFilter(types)::iterator;
//	}
//
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private Stream<SimulationEntity> streamFilter(Class... types)
//	{
//		return this.entities.stream().filter(e -> hasAll(e, types));
//	}
//
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private static boolean hasAll(SimulationEntity e, Class... cmps)
//	{
//		for (int i = 0; i < cmps.length; i++)
//		{
//			if (!e.has(cmps[i]))
//				return false;
//		}
//		return true;
//	}

	

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#getMinX()
	 */
	@Override
	public int getMinX()
	{
		return this.minX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#getMaxX()
	 */
	@Override
	public int getMaxX()
	{
		return this.maxX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#getMinY()
	 */
	@Override
	public int getMinY()
	{
		return this.minY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#getMaxY()
	 */
	@Override
	public int getMaxY()
	{
		return this.maxY;
	}


	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#getName()
	 */
	@Override
	public String getName()
	{
		return this.floorName;
	}
	public int getXlength()
	{
		return xlength;
	}

	public int getYlength()
	{
		return ylength;
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.massisframework.massis.model.building.IFloor#remove(com.
//	 * massisframework.massis.model.building.SimulationObject)
//	 */
//	@Override
//	public void remove(Restorable simObj)
//	{
//		this.quadPilu.remove(simObj);
//
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.massisframework.massis.model.building.IFloor#addPerson(com.
//	 * massisframework.massis.model.building.SimulationObject)
//	 */
//	@Override
//	public void addPerson(Restorable simObj)
//	{
//		if (simObj instanceof LowLevelAgent)
//		{
//			this.quadPilu.insert((LowLevelAgent) simObj);
//		}
//
//	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.massisframework.massis.model.building.IFloor#findPath(com.
//	 * massisframework.massis.model.location.Location,
//	 * com.massisframework.massis.model.location.Location,
//	 * com.massisframework.massis.pathfinding.straightedge.FindPathResult)
//	 */
//	@Override
//	public void findPath(final LocationImpl fromLoc, LocationImpl to,
//			FindPathResult callback)
//	{
//		/*
//		 * If the target location has a different floor that the current
//		 * location, the path is generated to the nearest teleport.
//		 */
//		if (fromLoc.getFloor() != to.getFloor())
//		{
//			//
//			// final List<SimulationEntity> teleportsConnecting =
//			// getTeleportsConnectingFloor(
//			// to.getFloor());
//			// if (teleportsConnecting == null)
//			// {
//			// logInfo("No teleports connecting {0} with {1} ",
//			// new Object[] { fromLoc, to });
//			// callback.onError(
//			// FindPathResult.PathFinderErrorReason.UNREACHABLE_TARGET);
//			// // return null;
//			// } else
//			// {
//			// final SimulationEntity targetTeleport = teleportsConnecting
//			// .get(0);
//			//
//			// final Location targetLocation = targetTeleport
//			// .get(Location.class);
//			// this.pathFinder.findPath(fromLoc, targetLocation,
//			// targetTeleport.get(TeleportComponent.class), callback);
//			// }
//			throw new UnsupportedOperationException();
//
//		} else
//		{
//			this.pathFinder.findPath(fromLoc, to, null, callback);
//		}
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.massisframework.massis.model.building.IFloor#
//	 * getTeleportsConnectingFloor(com.massisframework.massis.model.building.
//	 * Floor)
//	 */
//	@Override
//	public Iterable<SimulationEntity> getTeleportsConnectingFloor(
//			final Floor other)
//	{
//
//		// if (!this.teleportConnectingFloors.containsKey(other))
//		// {
//		// final ArrayList<SimulationEntity> teleportsConnecting = new
//		// ArrayList<>();
//		// for (final SimulationEntity se : this.teleports)
//		// {
//		// TeleportComponent teleport = se.get(TeleportComponent.class);
//		// if (teleport.getTeleportType() == TeleportType.START && teleport
//		// .getDistanceToFloor(other) < Integer.MAX_VALUE)
//		// {
//		// teleportsConnecting.add(se);
//		// }
//		// }
//		// Collections.sort(teleportsConnecting, (o1, o2) -> {
//		// TeleportComponent t1 = o1.get(TeleportComponent.class);
//		// TeleportComponent t2 = o2.get(TeleportComponent.class);
//		//
//		// return Integer.compare(
//		// t1.getDistanceToFloor(other),
//		// t2.getDistanceToFloor(other));
//		// });
//		// this.teleportConnectingFloors.put(other,
//		// Collections.unmodifiableList(teleportsConnecting));
//		// }
//		// return this.teleportConnectingFloors.get(other);
//		return Collections.emptyList();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.massisframework.massis.model.building.IFloor#getTeleports()
//	 */
//	@Override
//	public List<SimulationEntity> getTeleports()
//	{
//		// return Collections.unmodifiableList(this.teleports);
//		return Collections.emptyList();
//	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.massisframework.massis.model.building.IFloor#getRoomConnectors()
//	 */
//	@Override
//	public List<RoomConnector> getRoomConnectors()
//	{
//		throw new UnsupportedOperationException();
//		// return Collections.unmodifiableList(this.roomConnectors);
//	}

	
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.massisframework.massis.model.building.IFloor#
//	 * getNearestPointOutsideOfObstacles(double, double)
//	 */
//	@Override
//	public KPoint getNearestPointOutsideOfObstacles(double x, double y)
//	{
//		return this.pathFinder
//				.getNearestPointOutsideOfObstacles(new KPoint(x, y));
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.massisframework.massis.model.building.IFloor#
//	 * getNearestPointOutsideOfObstacles(straightedge.geom.KPoint)
//	 */
//	@Override
//	public KPoint getNearestPointOutsideOfObstacles(KPoint p)
//	{
//		return this.pathFinder.getNearestPointOutsideOfObstacles(p);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.massisframework.massis.model.building.IFloor#getQTRectangles()
//	 */
//	@Override
//	public Iterable<KPolygon> getQTRectangles()
//	{
//
//		return this.quadPilu.getRectangles();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * com.massisframework.massis.model.building.IFloor#getAgentsInRange(int,
//	 * int, int, int)
//	 */
//	@Override
//	public Iterable<LowLevelAgent> getAgentsInRange(int xmin, int ymin,
//			int xmax,
//			int ymax)
//	{
//
//		final SearchRangeCallback rangeCallback = new SearchRangeCallback();
//		this.quadPilu.searchInRange(xmin, ymin, xmax, ymax, rangeCallback);
//		return rangeCallback.agents;
//
//	}
//
//	@SuppressWarnings("unchecked")
//	private HighLevelController createHLController(LowLevelAgent agent,
//			Map<String, String> metadata, String resourcesFolder)
//	{
//
//		/*
//		 * Avoid relative paths issues
//		 */
//		final String absResFolder = new File(resourcesFolder).getAbsolutePath();
//		final String className = metadata
//				.get(SimObjectProperty.CLASSNAME.toString());
//
//		HighLevelController hlc = HighLevelController.getDummyController();
//		if (className != null)
//		{
//			try
//			{
//				@SuppressWarnings("rawtypes")
//				final Class agentClass = Class.forName(className);
//
//				hlc = HighLevelController.newInstance(agentClass, agent,
//						metadata, absResFolder);
//
//			} catch (final ClassNotFoundException ex)
//			{
//				Logger.getLogger(FloorImpl.class.getName())
//						.log(java.util.logging.Level.SEVERE, null, ex);
//			}
//
//		}
//		return hlc;
//
//	}
//
//	/**
//	 * Utility class for searching in ranges and returning the results in a list
//	 *
//	 * @author rpax
//	 *
//	 */
//	private static class SearchRangeCallback
//			implements ArrayQuadTreeCallback<LowLevelAgent> {
//
//		private final ArrayList<LowLevelAgent> agents = new ArrayList<>();
//
//		@Override
//		public void query(LowLevelAgent element)
//		{
//			this.agents.add(element);
//		}
//
//		@Override
//		public boolean shouldStop()
//		{
//			return false;
//		}
//	}
//
//	private static void logInfo(String str, Object[] data)
//	{
//
//		Logger.getLogger(FloorImpl.class.getName())
//				.log(java.util.logging.Level.INFO, str, data);
//	}
//

//
//	private ArrayQuadTree<LowLevelAgent> getQTree()
//	{
//		if (this.quadPilu == null)
//		{
//			this.quadPilu = new ArrayQuadTree<>(7, this.minX, this.maxX,
//					this.minY,
//					this.maxY);
//		}
//		return this.quadPilu;
//	}
}

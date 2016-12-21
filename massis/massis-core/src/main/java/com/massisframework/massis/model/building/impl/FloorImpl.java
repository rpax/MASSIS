/**
 *
 */
package com.massisframework.massis.model.building.impl;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

import com.eteks.sweethome3d.model.HomeDoorOrWindow;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Level;
import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.agents.HighLevelController;
import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.building.RoomConnector;
import com.massisframework.massis.model.components.Location;
import com.massisframework.massis.model.components.RoomComponent;
import com.massisframework.massis.model.components.SimulationComponent;
import com.massisframework.massis.model.components.TeleportComponent;
import com.massisframework.massis.model.components.TeleportComponent.TeleportType;
import com.massisframework.massis.model.components.building.ShapeComponent;
import com.massisframework.massis.model.components.building.WallComponent;
import com.massisframework.massis.model.location.LocationImpl;
import com.massisframework.massis.model.location.SimLocation;
import com.massisframework.massis.pathfinding.straightedge.FindPathResult;
import com.massisframework.massis.pathfinding.straightedge.SEPathFinder;
import com.massisframework.massis.sim.SimulationEntity;
import com.massisframework.massis.util.Indexable;
import com.massisframework.massis.util.PathFindingUtils;
import com.massisframework.massis.util.SimObjectProperty;
import com.massisframework.massis.util.field.grid.quadtree.array.ArrayQuadTree;
import com.massisframework.massis.util.field.grid.quadtree.array.ArrayQuadTreeCallback;
import com.massisframework.massis.util.geom.ContainmentPolygon;
import com.massisframework.massis.util.io.Restorable;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;
import straightedge.geom.PolygonBufferer;
import straightedge.geom.path.PathBlockingObstacleImpl;

/**
 * Represents a level/Floor in the building
 *
 * @author rpax
 *
 */
public class FloorImpl implements Floor {

	/**
	 * The ID of this floor. Useful for hashcodes.
	 */
	private final int id;
	// UID "generator"
	private static final AtomicInteger CURRENT_FLOOR_MAX_ID = new AtomicInteger(
			0);

	private static int getNewUID()
	{
		return CURRENT_FLOOR_MAX_ID.getAndIncrement();
	}

	/*
	 * Bounds
	 */
	private int minX, maxX, minY, maxY, xlength, ylength;

	private final ArrayList<RoomConnector> roomConnectors = new ArrayList<>();
	/**
	 * Teleports linking to other floors
	 */
	private final HashMap<Floor, List<SimulationEntity>> teleportConnectingFloors = new HashMap<>();
	
	private List<SimulationEntity> entities;
	/**
	 * Polygons for using the containment behavior
	 */
	private List<ContainmentPolygon> containmentPolygons;

	/**
	 * The proper pathfinder
	 */
	private final SEPathFinder pathFinder;
	/**
	 * QuadTree
	 */
	private ArrayQuadTree<LowLevelAgent> quadPilu;
	private String floorName;

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
	public FloorImpl(String name)
	{

		this.id = getNewUID();
		this.floorName = name;
		/*
		 * Rooms & Walls initialization
		 */

		this.entities = new ArrayList<>();

		this.configureBounds();

		this.initializeSimObjects();
		/**
		 * @formatter:on
		 */
		this.pathFinder = new SEPathFinder(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.massisframework.massis.model.building.IFloor#initializePathFinder()
	 */
	@Override
	public void initializePathFinder()
	{
		this.pathFinder.initialize();

	}

	/**
	 * Initializes the furniture in this floor. Takes every piece of furniture
	 * in this floor and makes its MASSIS equivalent
	 */
	private void initializeSimFurniture()
	{
		for (final HomePieceOfFurniture f : this.furniture3D)
		{
			/*
			 * Creation of the location of the new element
			 */
			final SimLocation location = new SimLocation(f, this);
			/*
			 * Has metadata?
			 */
			final Map<String, String> metadata = this.building.getMetadata(f);
			/*
			 * Resources folder
			 */
			final String resourcesFolder = this.building.getResourcesFolder();
			/*
			 * Special case: teleports
			 */
			if (metadata.containsKey(SimObjectProperty.TELEPORT.toString()))
			{

				final TeleportImpl teleport = new TeleportImpl(metadata,
						location,
						this.building.getMovementManager(),
						this.building.getAnimationManager(),
						this.building.getEnvironmentManager(),
						this.building.getPathManager());
				this.building.addTeleport(teleport);
				this.teleports.add(teleport);
				this.roomConnectors.add(teleport);
				f.setVisible(true);

			} else
			{
				if (metadata.containsKey(
						SimObjectProperty.POINT_OF_INTEREST.toString()))
				{
					/*
					 * Is it an special place?
					 */
					this.building.addNamedLocation(
							metadata.get(SimObjectProperty.NAME.toString()),
							new LocationImpl(f.getX(), f.getY(), this));
				} else
				{
					if (f instanceof HomeDoorOrWindow)
					{
						/*
						 * Windows & doors are the same in SH3D but not in
						 * MASSIS.
						 */
						// Comprobar si es ventana o no
						if (f.getName() != null
								&& f.getName().toUpperCase().contains(
										SimObjectProperty.WINDOW.toString()))
						{
							final SimWindowImpl window = new SimWindowImpl(
									metadata, location,
									this.building.getMovementManager(),
									this.building.getAnimationManager(),
									this.building.getEnvironmentManager(),
									this.building.getPathManager());
							this.building.addSH3DRepresentation(window, f);
							this.windows.add(window);

						} else
						{
							final SimDoorImpl door = new SimDoorImpl(metadata,
									location,
									this.building.getMovementManager(),
									this.building.getAnimationManager(),
									this.building.getEnvironmentManager(),
									this.building.getPathManager());
							this.building.addSH3DRepresentation(door, f);
							this.doors.add(door);
							this.roomConnectors.add(door);
						}
					} else /* Should be an agent then */

					{
						/*
						 * Tries to build an agent, by its metadata.
						 */

						final LowLevelAgent person = new DefaultAgent(metadata,
								location, this.building.getMovementManager(),
								this.building.getAnimationManager(),
								this.building.getEnvironmentManager(),
								this.building.getPathManager());

						final HighLevelController hlc = createHLController(
								person,
								metadata, resourcesFolder);
						// What if it is only a chair?
						// Should be treated differently?
						this.building.addToSchedule(hlc);
						/*
						 * If the furniture parameters were right, add the agent
						 */

						this.building.addSH3DRepresentation(person, f);
						this.addPerson(person);

					}
				}
			}

		}
	}

	/**
	 * Creation of the containment polygons
	 */
	private void initializeContainmentPolygons()
	{
		this.containmentPolygons = new ArrayList<>();
		for (final SimulationEntity w : this
				.filterEntities(
						ShapeComponent.class,
						WallComponent.class))
		{
			// uh...
			Shape shape = w.get(ShapeComponent.class).getShape();
			KPolygon poly = PathFindingUtils.createKPolygonFromShape(shape);
			this.containmentPolygons.add(new ContainmentPolygon(
					new PolygonBufferer().buffer(poly, 50, 1)));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.massisframework.massis.model.building.IFloor#getContainmentPolygons()
	 */
	@Override
	public List<ContainmentPolygon> getContainmentPolygons()
	{
		return this.containmentPolygons;
	}

	/**
	 * Sets the maximum and minimum bounds TODO performance. Check only for new
	 * added items
	 */
	private void configureBounds()
	{
		this.minX = Integer.MAX_VALUE;
		this.minY = Integer.MAX_VALUE;
		this.maxX = Integer.MIN_VALUE;
		this.maxY = Integer.MIN_VALUE;
		for (final SimulationEntity r : this
				.filterEntities(ShapeComponent.class))
		{
			ShapeComponent s = r.get(ShapeComponent.class);
			Rectangle2D bounds = s.getBounds();
			minX = (int) Math.min(minX, bounds.getMinX());
			minY = (int) Math.min(minY, bounds.getMinY());
			maxX = (int) Math.max(maxX, bounds.getMaxX());
			maxY = (int) Math.max(maxY, bounds.getMaxY());

		}

		minX -= 1;
		minY -= 1;
		maxX += 1;
		maxY += 1;
		/*
		 * Prevent zero length bounds
		 */
		if (maxX - minX <= 0)
		{
			minX = 0;
			maxX = 1;
		}
		if (maxY - minY <= 0)
		{
			minY = 0;
			maxY = 1;
		}

		this.xlength = this.maxX - this.minX;
		this.ylength = this.maxY - this.minY;

		this.quadPilu = new ArrayQuadTree<>(7, this.minX, this.maxX, this.minY,
				this.maxY);
	}

	/**
	 * FIXME this is very inneficient and obscure
	 * 
	 * @deprecated
	 * @param type
	 * @return
	 */
	private <T extends SimulationComponent> Iterable<T> filterComponents(
			Class<T> type)
	{
		return StreamSupport
				.stream(this.entities.spliterator(), false)
				.map(e -> e.get(type))
				.filter(c -> c != null)
				.map(type::cast)::iterator;
	}

	/**
	 * FIXME this is very inneficient and obscure
	 * 
	 * @deprecated
	 * @param type
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Iterable<SimulationEntity> filterEntities(Class... types)
	{
		return StreamSupport
				.stream(this.entities.spliterator(), false)
				.filter(e -> hasAll(e, types))::iterator;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static boolean hasAll(SimulationEntity e, Class... cmps)
	{
		for (int i = 0; i < cmps.length; i++)
		{
			if (!e.has(cmps[i]))
				return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#getRandomRoom()
	 */
	@Override
	public SimulationEntity getRandomRoom()
	{

		final SimulationEntity room = this.rooms
				.get(ThreadLocalRandom.current().nextInt(this.rooms.size()));
		return room;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#getDoors()
	 */
	@Override
	public Iterable<SimulationEntity> getDoors()
	{
		return Collections.unmodifiableList(this.doors);
	}

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
	 * @see com.massisframework.massis.model.building.IFloor#getWalls()
	 */
	@Override
	public Iterable<SimulationEntity> getWalls()
	{
		return this.filterEntities(WallComponent.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#getRooms()
	 */
	@Override
	public final Iterable<SimulationEntity> getRooms()
	{
		return this.filterEntities(RoomComponent.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#getAgents()
	 */
	@Override
	public Iterable<LowLevelAgent> getAgents()
	{
		return this.quadPilu.getElementsIn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.id;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.massisframework.massis.model.building.IFloor#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof Indexable))
		{
			return false;
		}
		final Indexable other = (Indexable) obj;
		if (this.id != other.getID())
		{
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.massisframework.massis.model.building.IFloor#getStationaryObstacles()
	 */
	@Override
	public Iterable<PathBlockingObstacleImpl> getStationaryObstacles()
	{
		return this.pathFinder.getStationaryObstacles();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#getWalkableAreas()
	 */
	@Override
	public Iterable<KPolygon> getWalkableAreas()
	{
		return this.pathFinder.getWalkableAreas();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#getLevel()
	 */
	@Override
	public Level getLevel()
	{
		return this.level3D;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#remove(com.
	 * massisframework.massis.model.building.SimulationObject)
	 */
	@Override
	public void remove(Restorable simObj)
	{
		this.quadPilu.remove(simObj);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#addPerson(com.
	 * massisframework.massis.model.building.SimulationObject)
	 */
	@Override
	public void addPerson(Restorable simObj)
	{
		if (simObj instanceof LowLevelAgent)
		{
			this.quadPilu.insert((LowLevelAgent) simObj);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#findPath(com.
	 * massisframework.massis.model.location.Location,
	 * com.massisframework.massis.model.location.Location,
	 * com.massisframework.massis.pathfinding.straightedge.FindPathResult)
	 */
	@Override
	public void findPath(final LocationImpl fromLoc, LocationImpl to,
			FindPathResult callback)
	{
		/*
		 * If the target location has a different floor that the current
		 * location, the path is generated to the nearest teleport.
		 */
		if (fromLoc.getFloor() != to.getFloor())
		{

			final List<SimulationEntity> teleportsConnecting = getTeleportsConnectingFloor(
					to.getFloor());
			if (teleportsConnecting == null)
			{
				logInfo("No teleports connecting {0} with {1} ",
						new Object[] { fromLoc, to });
				callback.onError(
						FindPathResult.PathFinderErrorReason.UNREACHABLE_TARGET);
				// return null;
			} else
			{
				final SimulationEntity targetTeleport = teleportsConnecting
						.get(0);

				final Location targetLocation = targetTeleport
						.get(Location.class);
				this.pathFinder.findPath(fromLoc, targetLocation,
						targetTeleport.get(TeleportComponent.class), callback);
			}

		} else
		{
			this.pathFinder.findPath(fromLoc, to, null, callback);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#
	 * getTeleportsConnectingFloor(com.massisframework.massis.model.building.
	 * Floor)
	 */
	@Override
	public List<SimulationEntity> getTeleportsConnectingFloor(final Floor other)
	{

		if (!this.teleportConnectingFloors.containsKey(other))
		{
			final ArrayList<SimulationEntity> teleportsConnecting = new ArrayList<>();
			for (final SimulationEntity se : this.teleports)
			{
				TeleportComponent teleport = se.get(TeleportComponent.class);
				if (teleport.getTeleportType() == TeleportType.START && teleport
						.getDistanceToFloor(other) < Integer.MAX_VALUE)
				{
					teleportsConnecting.add(se);
				}
			}
			Collections.sort(teleportsConnecting, (o1, o2) -> {
				TeleportComponent t1 = o1.get(TeleportComponent.class);
				TeleportComponent t2 = o2.get(TeleportComponent.class);

				return Integer.compare(
						t1.getDistanceToFloor(other),
						t2.getDistanceToFloor(other));
			});
			this.teleportConnectingFloors.put(other,
					Collections.unmodifiableList(teleportsConnecting));
		}
		return this.teleportConnectingFloors.get(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#getTeleports()
	 */
	@Override
	public List<SimulationEntity> getTeleports()
	{
		return Collections.unmodifiableList(this.teleports);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#getRoomConnectors()
	 */
	@Override
	public List<RoomConnector> getRoomConnectors()
	{
		return Collections.unmodifiableList(this.roomConnectors);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#getID()
	 */
	@Override
	public int getID()
	{
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#
	 * getNearestPointOutsideOfObstacles(double, double)
	 */
	@Override
	public KPoint getNearestPointOutsideOfObstacles(double x, double y)
	{
		return this.pathFinder
				.getNearestPointOutsideOfObstacles(new KPoint(x, y));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#
	 * getNearestPointOutsideOfObstacles(straightedge.geom.KPoint)
	 */
	@Override
	public KPoint getNearestPointOutsideOfObstacles(KPoint p)
	{
		return this.pathFinder.getNearestPointOutsideOfObstacles(p);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.building.IFloor#getQTRectangles()
	 */
	@Override
	public Iterable<KPolygon> getQTRectangles()
	{

		return this.quadPilu.getRectangles();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.massisframework.massis.model.building.IFloor#getAgentsInRange(int,
	 * int, int, int)
	 */
	@Override
	public Iterable<LowLevelAgent> getAgentsInRange(int xmin, int ymin,
			int xmax,
			int ymax)
	{

		final SearchRangeCallback rangeCallback = new SearchRangeCallback();
		this.quadPilu.searchInRange(xmin, ymin, xmax, ymax, rangeCallback);
		return rangeCallback.agents;

	}

	@SuppressWarnings("unchecked")
	private HighLevelController createHLController(LowLevelAgent agent,
			Map<String, String> metadata, String resourcesFolder)
	{

		/*
		 * Avoid relative paths issues
		 */
		final String absResFolder = new File(resourcesFolder).getAbsolutePath();
		final String className = metadata
				.get(SimObjectProperty.CLASSNAME.toString());

		HighLevelController hlc = HighLevelController.getDummyController();
		if (className != null)
		{
			try
			{
				@SuppressWarnings("rawtypes")
				final Class agentClass = Class.forName(className);

				hlc = HighLevelController.newInstance(agentClass, agent,
						metadata, absResFolder);

			} catch (final ClassNotFoundException ex)
			{
				Logger.getLogger(FloorImpl.class.getName())
						.log(java.util.logging.Level.SEVERE, null, ex);
			}

		}
		return hlc;

	}

	/**
	 * Utility class for searching in ranges and returning the results in a list
	 *
	 * @author rpax
	 *
	 */
	private static class SearchRangeCallback
			implements ArrayQuadTreeCallback<LowLevelAgent> {

		private final ArrayList<LowLevelAgent> agents = new ArrayList<>();

		@Override
		public void query(LowLevelAgent element)
		{
			this.agents.add(element);
		}

		@Override
		public boolean shouldStop()
		{
			return false;
		}
	}

	private static void logInfo(String str, Object[] data)
	{

		Logger.getLogger(FloorImpl.class.getName())
				.log(java.util.logging.Level.INFO, str, data);
	}

	public int getXlength()
	{
		return xlength;
	}

	public int getYlength()
	{
		return ylength;
	}
}

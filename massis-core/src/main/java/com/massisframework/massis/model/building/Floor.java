/**
 *
 */
package com.massisframework.massis.model.building;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.location.SimLocation;
import com.massisframework.massis.model.managers.movement.Path;
import com.massisframework.massis.util.Indexable;
import com.massisframework.massis.util.field.grid.quadtree.array.ArrayQuadTree;
import com.massisframework.massis.util.field.grid.quadtree.array.ArrayQuadTreeCallback;
import com.massisframework.massis.util.geom.ContainmentPolygon;
import com.massisframework.massis.pathfinding.straightedge.SEPathFinder;
import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;
import straightedge.geom.PolygonBufferer;
import straightedge.geom.path.PathBlockingObstacleImpl;

import com.eteks.sweethome3d.model.HomeDoorOrWindow;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Level;
import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.model.Wall;
import java.io.File;
import java.util.logging.Logger;
import com.massisframework.massis.model.agents.HighLevelController;
import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.util.SimObjectProperty;

/**
 * Represents a level/Floor in the building
 *
 * @author rpax
 *
 */
public class Floor implements Indexable {

	/**
	 * The ID of this floor. Useful for hashcodes.
	 */
	private final int id;
	// UID "generator"
	private static final AtomicInteger CURRENT_FLOOR_MAX_ID = new AtomicInteger(0);

	private static int getNewUID() {
		return CURRENT_FLOOR_MAX_ID.getAndIncrement();
	}

	/*
	 * SH3D objects
	 */
	private final com.eteks.sweethome3d.model.Level level3D;
	private final ArrayList<Room> rooms3D;
	private final ArrayList<Wall> walls3D;
	private final ArrayList<HomePieceOfFurniture> furniture3D;
	private final Building building;
	/*
	 * Bounds
	 */
	public final int minX, maxX, minY, maxY, xlength, ylength;

	/*
	 * Rooms & doors
	 */
	private final List<SimDoor> doors = new ArrayList<>();
	private final List<SimWindow> windows = new ArrayList<>();
	private final ArrayList<RoomConnector> roomConnectors = new ArrayList<>();
	/**
	 * Teleports linking to other floors
	 */
	private final HashMap<Floor, List<Teleport>> teleportConnectingFloors = new HashMap<>();
	/**
	 * MASSIS Walls
	 */
	private final ArrayList<SimWall> walls;
	/**
	 * MASSIS Rooms
	 */
	private final ArrayList<SimRoom> rooms;
	/**
	 * Polygons for using the containment behavior
	 */
	private ArrayList<ContainmentPolygon> containmentPolygons;
	/**
	 * Teleports in this Floor
	 */
	private final ArrayList<Teleport> teleports;
	/**
	 * The proper pathfinder
	 */
	private final SEPathFinder pathFinder;
	/**
	 * QuadTree
	 */
	private final ArrayQuadTree<DefaultAgent> quadPilu;

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
	public Floor(com.eteks.sweethome3d.model.Level level3D, ArrayList<Room> rooms3D, ArrayList<Wall> walls3D,
			ArrayList<HomePieceOfFurniture> furniture3D, Building building) {

		this.id = getNewUID();
		this.building = building;
		this.level3D = level3D;

		this.rooms3D = rooms3D;
		this.walls3D = walls3D;
		this.furniture3D = furniture3D;

		/*
		 * Rooms & Walls initialization
		 */

		this.walls = new ArrayList<>(this.walls3D.size());
		this.rooms = new ArrayList<>(this.rooms3D.size());
		this.teleports = new ArrayList<>();
		/**
		 * TODO Shouldn't be in a Logger or similar?
		 *
		 * @formatter:off
		 */
		System.err.println("======================================================");
		System.err.println("Creating floor from level [" + this.level3D.getName() + "]");
		int[] bounds = this.configureBounds();
		this.minX = bounds[0];
		this.minY = bounds[1];
		this.maxX = bounds[2];
		this.maxY = bounds[3];
		this.xlength = this.maxX - this.minX;
		this.ylength = this.maxY - this.minY;
		this.quadPilu = new ArrayQuadTree<>(7, this.minX, this.maxX, this.minY, this.maxY);
		System.err.println("Initializing simulation objects..");
		this.initializeSimObjects();
		System.err.println("# of SimulationObjects: " + this.quadPilu.countElements());
		System.err.println("# of rooms: " + this.getRooms().size());
		System.err.println("======================================================");
		/**
		 * @formatter:on
		 */
		this.pathFinder = new SEPathFinder(this);
	}

	public void initializePathFinder() {
		this.pathFinder.initialize();

	}

	private void initializeSimObjects() {
		// First rooms
		initializeRooms();
		// Then walls
		initializeWalls();
		// Once the walls are built, the containment polygons
		initializeContainmentPolygons();
		// finally the furniture
		initializeSimFurniture();
	}

	/**
	 * Initializes the furniture in this floor. Takes every piece of furniture
	 * in this floor and makes its MASSIS equivalent
	 */
	private void initializeSimFurniture() {
		for (HomePieceOfFurniture f : this.furniture3D) {
			/*
			 * Creation of the location of the new element
			 */
			SimLocation location = new SimLocation(f, this);
			/*
			 * Has metadata?
			 */
			Map<String, String> metadata = building.getMetadata(f);
			/*
			 * Resources folder
			 */
			final String resourcesFolder = this.building.getResourcesFolder();
			/*
			 * Special case: teleports
			 */
			if (metadata.containsKey(SimObjectProperty.TELEPORT.toString())) {

				Teleport teleport = new Teleport(metadata, location, this.building.getMovementManager(),
						this.building.getAnimationManager(), this.building.getEnvironmentManager());
				this.building.addTeleport(teleport);
				this.teleports.add(teleport);
				this.roomConnectors.add(teleport);
				f.setVisible(true);

			} else {
				if (metadata.containsKey(SimObjectProperty.POINT_OF_INTEREST.toString())) {
					/*
					 * Is it an special place?
					 */
					this.building.addNamedLocation(metadata.get(SimObjectProperty.NAME.toString()),
							new Location(f.getX(), f.getY(), this));
				} else {
					if (f instanceof HomeDoorOrWindow) {
						/*
						 * Windows & doors are the same in SH3D but not in
						 * MASSIS.
						 */
						// Comprobar si es ventana o no
						if (f.getName() != null
								&& f.getName().toUpperCase().contains(SimObjectProperty.WINDOW.toString())) {
							SimWindow window = new SimWindow(metadata, location, this.building.getMovementManager(),
									this.building.getAnimationManager(), this.building.getEnvironmentManager());
							this.building.addSH3DRepresentation(window, f);
							this.windows.add(window);

						} else {
							SimDoor door = new SimDoor(metadata, location, this.building.getMovementManager(),
									this.building.getAnimationManager(), this.building.getEnvironmentManager());
							this.building.addSH3DRepresentation(door, f);
							this.doors.add(door);
							this.roomConnectors.add(door);
						}
					} else /* Should be an agent then */

					{
						/*
						 * Tries to build an agent, by its metadata.
						 */

						DefaultAgent person = new DefaultAgent(metadata, location, this.building.getMovementManager(),
								this.building.getAnimationManager(), this.building.getEnvironmentManager());

						HighLevelController hlc = createHLController(person, metadata, resourcesFolder);

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
	 * Wall initialization
	 */
	private void initializeWalls() {
		for (Wall w : this.walls3D) {
			SimLocation location = new SimLocation(w, this);
			Map<String, String> metadata = this.building.getMetadata(w);
			SimWall simWall = new SimWall(metadata, location, this.building.getMovementManager(),
					this.building.getAnimationManager(), this.building.getEnvironmentManager());

			this.walls.add(simWall);
		}

	}

	/**
	 * Room initialization
	 */
	private void initializeRooms() {
		for (Room r : this.rooms3D) {
			SimLocation location = new SimLocation(r, this);

			Map<String, String> metadata = this.building.getMetadata(r);

			SimRoom simRoom = new SimRoom(metadata, location, this.building.getMovementManager(),
					this.building.getAnimationManager(), this.building.getEnvironmentManager());
			/*
			 * If has a name, must be added to the named rooms section
			 */
			if (r.getName() != null) {
				this.building.addNamedRoom(r.getName(), simRoom);
			}
			this.rooms.add(simRoom);
		}

	}

	/**
	 * Creation of the containment polygons
	 */
	private void initializeContainmentPolygons() {
		this.containmentPolygons = new ArrayList<>();
		for (SimWall w : this.walls) {
			this.containmentPolygons.add(new ContainmentPolygon(new PolygonBufferer().buffer(w.getPolygon(), 50, 1)));
		}

	}

	public ArrayList<ContainmentPolygon> getContainmentPolygons() {
		return containmentPolygons;
	}

	/**
	 * Sets the maximum and minimum bounds
	 */
	private int[] configureBounds() {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (Room r : this.rooms3D) {
			float[][] points = r.getPoints();
			for (int i = 0; i < points.length; i++) {
				minX = (int) Math.min(minX, Math.floor(points[i][0]));
				minY = (int) Math.min(minY, Math.floor(points[i][1]));
				maxX = (int) Math.max(maxX, Math.ceil(points[i][0]));
				maxY = (int) Math.max(maxY, Math.ceil(points[i][1]));
			}
		}
		for (Wall w : this.walls3D) {
			float[][] points = w.getPoints();
			for (int i = 0; i < points.length; i++) {
				minX = (int) Math.min(minX, Math.floor(points[i][0]));
				minY = (int) Math.min(minY, Math.floor(points[i][1]));
				maxX = (int) Math.max(maxX, Math.ceil(points[i][0]));
				maxY = (int) Math.max(maxY, Math.ceil(points[i][1]));
			}
		}
		for (HomePieceOfFurniture f : this.furniture3D) {
			float[][] points = f.getPoints();
			for (int i = 0; i < points.length; i++) {
				minX = (int) Math.min(minX, Math.floor(points[i][0]));
				minY = (int) Math.min(minY, Math.floor(points[i][1]));
				maxX = (int) Math.max(maxX, Math.ceil(points[i][0]));
				maxY = (int) Math.max(maxY, Math.ceil(points[i][1]));
			}
		}
		minX -= 1;
		minY -= 1;
		maxX += 1;
		maxY += 1;
		/*
		 * Prevent zero length bounds
		 */
		if (maxX - minX <= 0) {
			minX = 0;
			maxX = 1;
		}
		if (maxY - minY <= 0) {
			minY = 0;
			maxY = 1;
		}
		return new int[] { minX, minY, maxX, maxY };
	}

	/**
	 *
	 * @return a random Room in this floor
	 */
	public SimRoom getRandomRoom() {

		SimRoom room = this.rooms.get(ThreadLocalRandom.current().nextInt(this.rooms.size()));
		return room;
	}

	public List<SimDoor> getDoors() {
		return Collections.unmodifiableList(this.doors);
	}

	public int getMinX() {
		return minX;
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMinY() {
		return minY;
	}

	public int getMaxY() {
		return maxY;
	}

	public List<SimWall> getWalls() {
		return Collections.unmodifiableList(this.walls);
	}

	public final List<SimRoom> getRooms() {
		return rooms;
	}

	public Iterable<DefaultAgent> getPeople() {
		return this.quadPilu.getElementsIn();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Floor)) {
			return false;
		}
		Floor other = (Floor) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	public Iterable<PathBlockingObstacleImpl> getStationaryObstacles() {
		return this.pathFinder.getStationaryObstacles();
	}

	public Iterable<KPolygon> getWalkableAreas() {
		return this.pathFinder.getWalkableAreas();
	}

	public String getName() {
		return this.level3D.getName();
	}

	public Level getLevel() {
		return this.level3D;
	}

	/**
	 * Removes an agent from this floor
	 *
	 * @param simObj
	 *            the agent to be removed
	 */
	public void remove(SimulationObject simObj) {
		this.quadPilu.remove(simObj);

	}

	/**
	 * Adds an agent to this floor
	 *
	 * @param simObj
	 */
	public void addPerson(SimulationObject simObj) {
		if (simObj instanceof DefaultAgent) {
			this.quadPilu.insert((DefaultAgent) simObj);
		}

	}

	/**
	 * Finds a path in this floor. If the
	 *
	 * @param fromLoc
	 *            the starting location
	 * @param to
	 *            the desired location
	 * @return the path.
	 */
	public Path findPath(final Location fromLoc, Location to) {
		/*
		 * If the target location has a different floor that the current
		 * location, the path is generated to the nearest teleport.
		 */
		if (fromLoc.getFloor() != to.getFloor()) {

			List<Teleport> teleportsConnecting = getTeleportsConnectingFloor(to.getFloor());
			if (teleportsConnecting == null) {
				logInfo("No teleports connecting {0} with {1} ", new Object[] { fromLoc, to });
				return null;
			}
			Teleport targetTeleport = teleportsConnecting.get(0);
			final Location targetLocation = targetTeleport.getLocation();
			Path path = this.pathFinder.findPath(fromLoc, targetLocation);
			if (path == null) {
				return null;
			} else {
				return new Path(path.getPoints(), targetTeleport);
			}
		} else {
			return this.pathFinder.findPath(fromLoc, to);
		}
	}

	/**
	 * Returns the available teleports in this floor that can be used to reach
	 * other floor
	 *
	 * @param other
	 *            the target floor
	 * @return a list of teleports that can be used to reach the other floor
	 */
	public List<Teleport> getTeleportsConnectingFloor(final Floor other) {

		if (!this.teleportConnectingFloors.containsKey(other)) {
			ArrayList<Teleport> teleportsConnecting = new ArrayList<>();
			for (Teleport teleport : this.teleports) {
				if (teleport.getType() == Teleport.START && teleport.getDistanceToFloor(other) < Integer.MAX_VALUE) {
					teleportsConnecting.add(teleport);
				}
			}
			Collections.sort(teleportsConnecting, new Comparator<Teleport>() {
				@Override
				public int compare(Teleport o1, Teleport o2) {
					return Integer.compare(o1.getDistanceToFloor(other), o2.getDistanceToFloor(other));
				}
			});
			this.teleportConnectingFloors.put(other, Collections.unmodifiableList(teleportsConnecting));
		}
		return this.teleportConnectingFloors.get(other);
	}

	public List<Teleport> getTeleports() {
		return Collections.unmodifiableList(this.teleports);
	}

	public List<RoomConnector> getRoomConnectors() {
		return Collections.unmodifiableList(roomConnectors);
	}

	@Override
	public int getID() {
		return this.id;
	}

	public KPoint getNearestPointOutsideOfObstacles(double x, double y) {
		return this.pathFinder.getNearestPointOutsideOfObstacles(new KPoint(x, y));
	}

	public KPoint getNearestPointOutsideOfObstacles(KPoint p) {
		return this.pathFinder.getNearestPointOutsideOfObstacles(p);
	}

	/**
	 *
	 * @return the rectangles of the leaves of the QuadTree.
	 */
	public Iterable<KPolygon> getQTRectangles() {

		return this.quadPilu.getRectangles();
	}

	/**
	 *
	 * @param xmin
	 * @param ymin
	 * @param xmax
	 * @param ymax
	 * @return The agents inside the rectangle defined by xmin,ymin,xmax,ymax
	 */
	public Iterable<DefaultAgent> getAgentsInRange(int xmin, int ymin, int xmax, int ymax) {

		SearchRangeCallback rangeCallback = new SearchRangeCallback();
		this.quadPilu.searchInRange(xmin, ymin, xmax, ymax, rangeCallback);
		return rangeCallback.agents;

	}

	@SuppressWarnings("unchecked")
	private HighLevelController createHLController(LowLevelAgent agent, Map<String, String> metadata,
			String resourcesFolder) {

		/*
		 * Avoid relative paths issues
		 */
		final String absResFolder = new File(resourcesFolder).getAbsolutePath();
		final String className = metadata.get(SimObjectProperty.CLASSNAME.toString());

		HighLevelController hlc = HighLevelController.getDummyController();
		if (className != null) {
			try {
				@SuppressWarnings("rawtypes")
				Class agentClass = Class.forName(className);

				hlc = HighLevelController.newInstance(agentClass, agent, metadata, absResFolder);

			} catch (ClassNotFoundException ex) {
				Logger.getLogger(Floor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
	private static class SearchRangeCallback implements ArrayQuadTreeCallback<DefaultAgent> {

		private final ArrayList<DefaultAgent> agents = new ArrayList<>();

		@Override
		public void query(DefaultAgent element) {
			agents.add(element);
		}

		@Override
		public boolean shouldStop() {
			return false;
		}
	}

	private static void logInfo(String str, Object[] data) {

		Logger.getLogger(Floor.class.getName()).log(java.util.logging.Level.INFO, str, data);
	}
}

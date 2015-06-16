package rpax.massis.model.building;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import rpax.massis.displays.SimulationDisplay;
import rpax.massis.model.agents.Agent;
import rpax.massis.model.location.Location;
import rpax.massis.model.managers.AnimationManager;
import rpax.massis.model.managers.EnvironmentManager;
import rpax.massis.model.managers.movement.MovementManager;
import rpax.massis.sh3d.plugins.metadata.MASSISHomeMetadataManager;
import rpax.massis.sim.AbstractSimulation;

import com.eteks.sweethome3d.io.HomeFileRecorder;
import com.eteks.sweethome3d.model.Elevatable;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Level;
import com.eteks.sweethome3d.model.RecorderException;
import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.model.Wall;

/**
 * Represents a building in the simulation environment.
 * 
 * @author rpax
 * 
 */
public class Building {
	/**
	 * SH3D building representation
	 */
	protected final Home home;
	/**
	 * Map linking the MASSIS' floors with SH3D levels
	 */
	protected HashMap<Level, Floor> levelsFloors;
	/**
	 * Map linking MASSIS {@link SimulationObject}s with SweetHome3D furniture.
	 */
	private final HashMap<SimulationObject, HomePieceOfFurniture> representationMap;

	/**
	 * List of the building's floors
	 */
	protected final ArrayList<Floor> floors;
	/**
	 * Resources folder of the simulation
	 */
	private final String resourcesFolder;
	/**
	 * The teleports of the building (stairs, basically)
	 */
	private final ArrayList<Teleport> allTeleports = new ArrayList<>();
	/**
	 * Map which links the name of the teleport with the teleport itself. this
	 * is needed because the teleports are not added in order, and it is needed
	 * to keep track of the first teleport element found
	 */
	private final HashMap<String, Teleport> teleportMap = new HashMap<>();

	// Managers
	private final MovementManager movement;
	private final AnimationManager animation;
	private final EnvironmentManager environment;
	//
	/**
	 * Map of the named locations of the building. POI & more
	 */
	private final Map<String, Location> namedLocations = new HashMap<>();
	/**
	 * Map with the rooms names . It is useful for making an agent to go to an
	 * specific room
	 */
	private final Map<String, SimRoom> namedRooms = new HashMap<>();

	/**
	 * 
	 * @param home
	 *            a SweetHome3D home
	 * @param resourcesFolder
	 *            the simulation resourcdes folder
	 * @param progressMonitor
	 *            a progress monitor which tracks the progress of the loading of
	 *            the building
	 */
	private Building(Home home, String resourcesFolder,
			BuildingProgressMonitor progressMonitor) {
		this.resourcesFolder = resourcesFolder;
		// Initial message
		progressMonitor.onUpdate(0, "Loading building");

		this.home = home;
		/*
		 * First thing when loading the building. Must be done the first thing
		 * done
		 */
		MASSISHomeMetadataManager.getHomeMetaData(home);
		/*
		 * Lock the base plan -> Increases speed
		 */
		home.setBasePlanLocked(true);
		/*
		 * Initialization
		 */
		this.representationMap = new HashMap<>();
		this.floors = new ArrayList<>();
		this.movement = new MovementManager();
		this.animation = new AnimationManager();
		this.environment = new EnvironmentManager(this);
		// Initial message
		progressMonitor.onUpdate(1, "Building loaded");
		/*
		 * Transform SH3D home into MASSIS Representation
		 */
		this.build(progressMonitor);
	}

	/**
	 * Creates a MASSIS building
	 * 
	 * @param state
	 *            The simulation
	 * @param homeFilePath
	 *            path of the building file
	 * @param resourcesFolder
	 *            path of the resources folder
	 * @param progressMonitor
	 *            a progress monitor which tracks the progress of the loading of
	 *            the building
	 * @throws RecorderException
	 *             if the Home throws any error when loading
	 */
	public Building(AbstractSimulation state, String homeFilePath,
			String resourcesFolder, BuildingProgressMonitor progressMonitor)
			throws RecorderException {
		this(new HomeFileRecorder().readHome(homeFilePath), resourcesFolder,
				progressMonitor);
	}

	/**
	 * Same as
	 * {@link #Building(AbstractSimulation, String, String, BuildingProgressMonitor)
	 * , without a {@link Building.BuildingProgressMonitor}
	 * 
	 * @param state
	 * @param homeFilePath
	 * @param resourcesFolder
	 * @throws RecorderException
	 */
	public Building(AbstractSimulation state, String homeFilePath,
			String resourcesFolder) throws RecorderException {
		this(new HomeFileRecorder().readHome(homeFilePath), resourcesFolder,
				new BuildingProgressMonitor() {

					@Override
					public void onFinished() {
					}

					@Override
					public void onUpdate(double progress, String msg) {
					}

				});
	}

	/**
	 * Links a simulationObject with its corresponding sweethome3d furniture
	 * element
	 * 
	 * @param simulationObject
	 *            the simulation object to be linked
	 * @param representation
	 *            the furniture element of sweethome3d that represents it
	 */
	public void addSH3DRepresentation(SimulationObject simulationObject,
			HomePieceOfFurniture representation) {
		this.representationMap.put(simulationObject, representation);
	}

	/**
	 * Adds a teleport element to the building
	 * 
	 * @param teleport
	 *            the teleport element
	 */
	public void addTeleport(Teleport teleport) {
		if (this.teleportMap.containsKey(teleport.getName()))
		{
			teleport.setConnection(this.teleportMap.get(teleport.getName()));
			this.teleportMap.get(teleport.getName()).setConnection(teleport);
			allTeleports.add(teleport);
			allTeleports.add(this.teleportMap.get(teleport.getName()));
			System.err.println("Linked " + teleport.getName());
			// Y se quita del mapa
			this.teleportMap.remove(teleport.getName());

		}
		else
		{
			this.teleportMap.put(teleport.getName(), teleport);
		}
	}

	public HomePieceOfFurniture getSH3DRepresentation(SimulationObject obj) {
		return this.representationMap.get(obj);
	}

	/**
	 * Constructs the building
	 * 
	 * @param progressMonitor
	 *            progress monitor, to show the state of the operation
	 */
	protected void build(final BuildingProgressMonitor progressMonitor) {
		/*
		 * Maps linking the sweethome3d level with the rooms, furniture and
		 * walls in it
		 */
		HashMap<Level, ArrayList<Room>> levelRooms = getLevelsElevatables(this.home
				.getRooms());
		HashMap<Level, ArrayList<Wall>> levelWalls = getLevelsElevatables(this.home
				.getWalls());
		HashMap<Level, ArrayList<HomePieceOfFurniture>> levelFurniture = getLevelsElevatables(this.home
				.getFurniture());
		this.levelsFloors = new HashMap<Level, Floor>();
		//
		progressMonitor.onUpdate(2, "Loading building");
		//
		for (Level level : home.getLevels())
		{
			if (!levelRooms.containsKey(level))
				levelRooms.put(level, new ArrayList<Room>());
			if (!levelWalls.containsKey(level))
				levelWalls.put(level, new ArrayList<Wall>());
			if (!levelFurniture.containsKey(level))
				levelFurniture
						.put(level, new ArrayList<HomePieceOfFurniture>());
			System.err.println("Creating level " + level.getName());
			Floor f = new Floor(level, levelRooms.get(level),
					levelWalls.get(level), levelFurniture.get(level), this);
			this.floors.add(f);
			this.levelsFloors.put(level, f);
		}
		//
		progressMonitor.onUpdate(3, "Linking floor connectors");
		//
		Teleport.computeTeleportDistances(this.allTeleports);
		//
		progressMonitor.onUpdate(5, "Linking floor connectors");
		//
		progressMonitor.onUpdate(5, "Preprocessing rooms");
		//
		int nrooms = 0;
		for (Floor f : this.getFloors())
			nrooms += f.getRooms().size();

		int roomNumber = 0;
		/*
		 * Room preprocessing. Every room has knowledge of the rooms around it,
		 * which makes faster the elements localization by room. If an agent was
		 * on a room, and there is not now, it is more probable that it is in
		 * the nearest room (BFS order).
		 */
		for (Floor f : this.getFloors())
		{
			for (SimRoom sr : f.getRooms())
			{
				progressMonitor.onUpdate(5 + roomNumber * 5.0D / nrooms,
						"Connecting rooms");
				sr.getRoomsOrderedByDistance();
				roomNumber++;
			}
		}
		/*
		 * Pathfinder creation.
		 */
		final AtomicInteger nfloor = new AtomicInteger(0);
		final StringBuffer sb = new StringBuffer();
		progressMonitor.onUpdate(10, "Processing floors");
		for (final Floor f : this.getFloors())
		{

			f.initializePathFinder();
			sb.append(f.getName() + ",");
			progressMonitor.onUpdate(10 + nfloor.get() * 90.0
					/ getFloors().size(), "<html>Processed: <br/>" + sb
					+ "</html>");
			nfloor.incrementAndGet();

		}
		progressMonitor.onUpdate(100, "Building created");
		progressMonitor.onFinished();

	}

	/**
	 * Links the levels with the elements on them.
	 */
	private static <T extends Elevatable> HashMap<Level, ArrayList<T>> getLevelsElevatables(
			Collection<T> elements) {
		HashMap<Level, ArrayList<T>> elevatables = new HashMap<>();
		for (T e : elements)
		{
			ArrayList<T> lvlElevatables = elevatables.get(e.getLevel());
			if (lvlElevatables == null)
			{
				lvlElevatables = new ArrayList<>();
				lvlElevatables.add(e);
				elevatables.put(e.getLevel(), lvlElevatables);
			}
			else
			{
				lvlElevatables.add(e);
			}
		}
		return elevatables;
	}

	public List<Floor> getFloors() {
		return Collections.unmodifiableList(floors);
	}

	public Home getHome() {
		return this.home;
	}

	public HashMap<Level, Floor> getLevelsFloors() {
		return levelsFloors;
	}

	public Floor getFloorOf(Level lvl) {
		return this.levelsFloors.get(lvl);
	}

	public Floor getFloorById(int floorId) {
		for (Floor f : this.getFloors())
		{
			if (f.getID() == floorId)
				return f;
		}
		return null;
	}

	/**
	 * TODO solo se usa para recuperar el estado
	 * 
	 * @param simObjId
	 * @return
	 */

	public SimulationObject getSimulationObject(int simObjId) {
		for (Floor f : this.getFloors())
		{
			for (Agent p : f.getPeople())
			{
				if (p.getID() == simObjId)
				{
					return p;
				}
			}
		}
		return null;

	}

	public SimRoom getRandomRoom() {
		Floor rndFloor = this.floors.get(ThreadLocalRandom.current().nextInt(
				this.floors.size()));
		return rndFloor.getRandomRoom();
	}

	public AnimationManager getAnimationManager() {
		return this.animation;
	}

	public EnvironmentManager getEnvironmentManager() {
		return this.environment;
	}

	public Location getNamedLocation(String name) {
		return this.namedLocations.get(name);
	}

	public void addNamedLocation(String name, Location location) {
		this.namedLocations.put(name, location);
	}

	public MovementManager getMovementManager() {
		return this.movement;
	}

	public void addNamedRoom(String name, SimRoom simRoom) {
		this.namedRooms.put(name, simRoom);
	}

	public void registerDisplays(SimulationDisplay... displays) {
		this.animation.add(displays);
	}

	public String getResourcesFolder() {
		return resourcesFolder;
	}

	public static interface BuildingProgressMonitor {
		public void onFinished();

		public void onUpdate(final double progress, final String msg);

	}
}

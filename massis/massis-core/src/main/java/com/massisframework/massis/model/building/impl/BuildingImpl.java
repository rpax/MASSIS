package com.massisframework.massis.model.building.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import com.eteks.sweethome3d.model.Elevatable;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Level;
import com.eteks.sweethome3d.model.RecorderException;
import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.model.Selectable;
import com.eteks.sweethome3d.model.Wall;
import com.massisframework.massis.displays.SimulationDisplay;
import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.agents.HighLevelController;
import com.massisframework.massis.model.building.BuildingProgressMonitor;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.building.ISimulationObject;
import com.massisframework.massis.model.building.Teleport;
import com.massisframework.massis.model.building.SimRoom;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.building.Building;
import com.massisframework.massis.model.building.SimulationObject;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.managers.AnimationManager;
import com.massisframework.massis.model.managers.EnvironmentManager;
import com.massisframework.massis.model.managers.movement.MovementManager;
import com.massisframework.massis.model.managers.pathfinding.PathFindingManager;
import com.massisframework.massis.sim.AbstractSimulation;
import com.massisframework.massis.util.SH3DUtils;
import com.massisframework.sweethome3d.metadata.HomeMetadataLoader;

/**
 * Represents a building in the simulation environment.
 *
 * @author rpax
 *
 */
public class BuildingImpl implements Building {

    protected Home home;
    /**
     * SH3D building representation
     */
    // protected final Home home;
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
    private final PathFindingManager pathManager;
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
    private final Collection<HighLevelController> scheduledControllers = new ArrayList<>();
	

    /**
     *
     *
     * @param resourcesFolder the simulation resourcdes folder
     * @param progressMonitor a progress monitor which tracks the progress of
     * the loading of the building
     */
    public BuildingImpl(Home home, String resourcesFolder,
            BuildingProgressMonitor progressMonitor)
    {
        this.resourcesFolder = resourcesFolder;
        // Initial message
        progressMonitor.onUpdate(0, "Loading building");

        this.home = home;

        /*
         * Lock the base plan -> Increases speed
         */
        this.home.setBasePlanLocked(true);
        /*
         * Initialization
         */
        this.representationMap = new HashMap<>();
        this.floors = new ArrayList<>();
        this.movement = new MovementManager();
        this.animation = new AnimationManager();
        this.environment = new EnvironmentManager(this);
        this.pathManager=new PathFindingManager();
        // Initial message
        progressMonitor.onUpdate(1, "Building loaded");
        /*
         * Transform SH3D home into MASSIS Representation
         */
        this.build(progressMonitor);
    }

    /**
     * Same as null null null null null     {@link #Building(AbstractSimulation, String, String, BuildingProgressMonitor)
	 * , without a {@link BuildingImpl.BuildingProgressMonitor}
     *
     * @param state
     * @param homeFilePath
     * @param resourcesFolder
     * @throws RecorderException
     */
    public BuildingImpl(Home home,
            String resourcesFolder) throws RecorderException
    {
        this(home, resourcesFolder,
                new BuildingProgressMonitor() {
            @Override
            public void onFinished()
            {
            }

            @Override
            public void onUpdate(double progress, String msg)
            {
            }
        });
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#addSH3DRepresentation(com.massisframework.massis.model.building.SimulationObject, com.eteks.sweethome3d.model.HomePieceOfFurniture)
	 */
    @Override
	public void addSH3DRepresentation(SimulationObject simulationObject,
            HomePieceOfFurniture representation)
    {
        this.representationMap.put(simulationObject, representation);
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#addTeleport(com.massisframework.massis.model.building.Teleport)
	 */
    @Override
	public void addTeleport(Teleport teleport)
    {
        if (this.teleportMap.containsKey(teleport.getName()))
        {
            teleport.setConnection(this.teleportMap.get(teleport.getName()));
            this.teleportMap.get(teleport.getName()).setConnection(teleport);
            this.allTeleports.add(teleport);
            this.allTeleports.add(this.teleportMap.get(teleport.getName()));
            System.err.println("Linked " + teleport.getName());
            // Y se quita del mapa
            this.teleportMap.remove(teleport.getName());

        } else
        {
            this.teleportMap.put(teleport.getName(), teleport);
        }
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#getSH3DRepresentation(com.massisframework.massis.model.building.SimulationObject)
	 */
    @Override
	public HomePieceOfFurniture getSH3DRepresentation(ISimulationObject obj)
    {
        return this.representationMap.get(obj);
    }

    /**
     * Constructs the building
     *
     * @param progressMonitor progress monitor, to show the state of the
     * operation
     */
    protected final void build(final BuildingProgressMonitor progressMonitor)
    {
        /*
         * Maps linking the sweethome3d level with the rooms, furniture and
         * walls in it
         */
        final HashMap<Level, ArrayList<Room>> levelRooms = getLevelsElevatables(
                this.getHome()
                .getRooms());
        final HashMap<Level, ArrayList<Wall>> levelWalls = getLevelsElevatables(
                this.getHome()
                .getWalls());
        final HashMap<Level, ArrayList<HomePieceOfFurniture>> levelFurniture = getLevelsElevatables(
                this.getHome()
                .getFurniture());
        this.levelsFloors = new HashMap<>();
        //
        progressMonitor.onUpdate(2, "Loading building");
        //
        final ArrayList<Level> levels = new ArrayList<>(this.getHome().getLevels());
        if (levels.isEmpty())
		{
			levels.add(null);
		}
        for (final Level level : levels)
        {
            if (!levelRooms.containsKey(level))
            {
                levelRooms.put(level, new ArrayList<Room>());
            }
            if (!levelWalls.containsKey(level))
            {
                levelWalls.put(level, new ArrayList<Wall>());
            }
            if (!levelFurniture.containsKey(level))
            {
                levelFurniture
                        .put(level, new ArrayList<HomePieceOfFurniture>());
            }
            System.err.println("Creating level " + SH3DUtils.getLevelName(level));
            final Floor f = new FloorImpl(level, levelRooms.get(level),
                    levelWalls.get(level), levelFurniture.get(level), this);
            this.floors.add(f);
            this.levelsFloors.put(level, f);
        }
        //
        progressMonitor.onUpdate(3, "Linking floor connectors");
        //
        TeleportImpl.computeTeleportDistances(this.allTeleports);
        //
        progressMonitor.onUpdate(5, "Linking floor connectors");
        //
        progressMonitor.onUpdate(5, "Preprocessing rooms");
        //
        int nrooms = 0;
        for (final Floor f : this.getFloors())
        {
            nrooms += f.getRooms().size();
        }

        int roomNumber = 0;
        /*
         * Room preprocessing. Every room has knowledge of the rooms around it,
         * which makes faster the elements localization by room. If an agent was
         * on a room, and there is not now, it is more probable that it is in
         * the nearest room (BFS order).
         */
        for (final Floor f : this.getFloors())
        {
            for (final SimRoom sr : f.getRooms())
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
            sb.append(f.getName()).append(",");
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
            Collection<T> elements)
    {
        final HashMap<Level, ArrayList<T>> elevatables = new HashMap<>();
        for (final T e : elements)
        {
            ArrayList<T> lvlElevatables = elevatables.get(e.getLevel());
            if (lvlElevatables == null)
            {
                lvlElevatables = new ArrayList<>();
                lvlElevatables.add(e);
                elevatables.put(e.getLevel(), lvlElevatables);
            } else
            {
                lvlElevatables.add(e);
            }
        }
        return elevatables;
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#getFloors()
	 */
    @Override
	public List<Floor> getFloors()
    {
        return Collections.unmodifiableList(this.floors);
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#getHome()
	 */
    @Override
	public Home getHome()
    {
        return this.home;
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#getLevelsFloors()
	 */
    @Override
	public HashMap<Level, Floor> getLevelsFloors()
    {
        return this.levelsFloors;
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#getFloorOf(com.eteks.sweethome3d.model.Level)
	 */
    @Override
	public Floor getFloorOf(Level lvl)
    {
        return this.levelsFloors.get(lvl);
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#getFloorById(int)
	 */
    @Override
	public Floor getFloorById(int floorId)
    {
        for (final Floor f : this.getFloors())
        {
            if (f.getID() == floorId)
            {
                return f;
            }
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#getSimulationObject(int)
	 */
    @Override
	public SimulationObject getSimulationObject(int simObjId)
    {
        for (final Floor f : this.getFloors())
        {
            for (final DefaultAgent p : f.getAgents())
            {
                if (p.getID() == simObjId)
                {
                    return p;
                }
            }
        }
        return null;

    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#getRandomRoom()
	 */
    @Override
	public SimRoom getRandomRoom()
    {
        final Floor rndFloor = this.floors.get(ThreadLocalRandom.current().nextInt(
                this.floors.size()));
        return rndFloor.getRandomRoom();
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#getAnimationManager()
	 */
    @Override
	public AnimationManager getAnimationManager()
    {
        return this.animation;
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#getEnvironmentManager()
	 */
    @Override
	public EnvironmentManager getEnvironmentManager()
    {
        return this.environment;
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#getNamedLocation(java.lang.String)
	 */
    @Override
	public Location getNamedLocation(String name)
    {
        return this.namedLocations.get(name);
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#addNamedLocation(java.lang.String, com.massisframework.massis.model.location.Location)
	 */
    @Override
	public void addNamedLocation(String name, Location location)
    {
        this.namedLocations.put(name, location);
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#getMovementManager()
	 */
    @Override
	public MovementManager getMovementManager()
    {
        return this.movement;
    }

    @Override
	public void addNamedRoom(String name, SimRoom simRoom)
    {
        this.namedRooms.put(name, simRoom);
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#registerDisplays(com.massisframework.massis.displays.SimulationDisplay)
	 */
    @Override
	public void registerDisplays(SimulationDisplay... displays)
    {
        this.animation.add(displays);
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#getResourcesFolder()
	 */
    @Override
	public String getResourcesFolder()
    {
        return this.resourcesFolder;
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#getMetadata(com.eteks.sweethome3d.model.Selectable)
	 */
    @Override
	public Map<String, String> getMetadata(Selectable f)
    {
        return HomeMetadataLoader.getBuildingMetadataManager(this.home).getMetadata(
                f);
        //this.buildingData.getMetadataManager().getMetadata(f);
    }
    @Override
    public void addToSchedule(HighLevelController hlc)
    {
        this.scheduledControllers.add(hlc);
    }

    /* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#getScheduledControllers()
	 */
    @Override
	public Collection<HighLevelController> getScheduledControllers()
    {
        return this.scheduledControllers;
    }


	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.building.IBuilding#getPathManager()
	 */
	@Override
	public PathFindingManager getPathManager() {
		return this.pathManager;
	}
}

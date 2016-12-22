//package com.massisframework.massis.model.components.building;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicLong;
//
//import com.eteks.sweethome3d.model.Elevatable;
//import com.eteks.sweethome3d.model.Home;
//import com.eteks.sweethome3d.model.HomeDoorOrWindow;
//import com.eteks.sweethome3d.model.HomePieceOfFurniture;
//import com.eteks.sweethome3d.model.Level;
//import com.eteks.sweethome3d.model.Room;
//import com.eteks.sweethome3d.model.Selectable;
//import com.eteks.sweethome3d.model.Wall;
//import com.massisframework.massis.model.building.Floor;
//import com.massisframework.massis.model.components.Location;
//import com.massisframework.massis.model.components.NameComponent;
//import com.massisframework.massis.model.components.TeleportComponent.TeleportType;
//import com.massisframework.massis.model.components.building.impl.FloorImpl;
//import com.massisframework.massis.model.components.building.impl.HashMetadataComponent;
//import com.massisframework.massis.model.components.building.impl.KPolygonShapeComponent;
//import com.massisframework.massis.model.components.building.impl.MovementCapabilititesImpl;
//import com.massisframework.massis.model.components.building.impl.RoomComponentImpl;
//import com.massisframework.massis.model.components.building.impl.SimplePhysicsComponent;
//import com.massisframework.massis.model.components.building.impl.TeleportComponentImpl;
//import com.massisframework.massis.model.components.building.impl.WallComponentImpl;
//import com.massisframework.massis.model.location.LocationImpl;
//import com.massisframework.massis.sim.SimulationEntity;
//import com.massisframework.massis.sim.engine.impl.DefaultSimulationEntity;
//import com.massisframework.massis.util.SimObjectProperty;
//import com.massisframework.sweethome3d.metadata.BuildingMetadataManager;
//import com.massisframework.sweethome3d.metadata.HomeMetadataLoader;
//
//public class SimulationEntityFactory {
//
//	private static final AtomicLong ID_GEN = new AtomicLong();
//	private static Map<Home, SimulationEntityFactory> factories;
//	//
//	private Home home;
//	private BuildingMetadataManager metadataManager;
//
//	private Map<String, SimulationEntity> teleportMap;
//	private Map<Level, Floor> floorMap;
//
//	private SimulationEntityFactory(Home home)
//	{
//		this.home = home;
//		this.metadataManager = HomeMetadataLoader
//				.getBuildingMetadataManager(home);
//		this.teleportMap = new HashMap<>();
//	}
//
//	public synchronized static SimulationEntityFactory get(Home home)
//	{
//
//		if (factories == null)
//		{
//			factories = new HashMap<>();
//		}
//		SimulationEntityFactory factory = factories.get(home);
//		if (factory == null)
//		{
//			factory = new SimulationEntityFactory(home);
//			factories.put(home, factory);
//		}
//
//		return factory;
//	}
//
//	public SimulationEntity createEntity()
//	{
//		return new DefaultSimulationEntity();
//	}
//
//	public SimulationEntity createWall(Wall wall)
//	{
//		SimulationEntity entity = createHomeEntity(wall);
//		entity.set(new WallComponentImpl());
//		return entity;
//	}
//
//	public SimulationEntity createFurniture(HomePieceOfFurniture f)
//	{
//		SimulationEntity entity = createHomeEntity(f);
//		// 1 add room component
//		entity.set(new RoomComponentImpl());
//		// 2. It is a teleport?
//		return entity;
//	}
//
//	/**
//	 * Links the levels with the elements on them.
//	 */
//	private static <T extends Elevatable> HashMap<Level, ArrayList<T>> getLevelsElevatables(
//			Collection<T> elements)
//	{
//		final HashMap<Level, ArrayList<T>> elevatables = new HashMap<>();
//		for (final T e : elements)
//		{
//			ArrayList<T> lvlElevatables = elevatables.get(e.getLevel());
//			if (lvlElevatables == null)
//			{
//				lvlElevatables = new ArrayList<>();
//				lvlElevatables.add(e);
//				elevatables.put(e.getLevel(), lvlElevatables);
//			} else
//			{
//				lvlElevatables.add(e);
//			}
//		}
//		return elevatables;
//	}
//
//	public SimulationEntity createRoom(Room room)
//	{
//		// 1 entity with default components
//		SimulationEntity entity = createHomeEntity(room);
//		return entity;
//	}
//
//	private SimulationEntity createHomeEntity(Selectable s)
//	{
//		SimulationEntity entity = createEntity();
//		// metadata
//		MetadataComponent mc = new HashMetadataComponent(
//				this.metadataManager.getMetadata(s));
//		entity.set(mc);
//		// Shape
//		KPolygonShapeComponent psc = new KPolygonShapeComponent(s.getPoints());
//		entity.set(psc);
//		// Coordinates
//		Location coord = new LocationImpl(psc.getPolygon().getCenter(),
//				getFloor(s));
//		entity.set(coord);
//		//
//		boolean dynamicDefVal = false;
//		boolean obstacleDefVal = true;
//		if (s instanceof Room)
//		{
//			obstacleDefVal = false;
//		}
//		if (s instanceof HomeDoorOrWindow)
//		{
//			obstacleDefVal = false;
//		}
//
//		boolean isDynamic = booleanValue(SimObjectProperty.IS_DYNAMIC,
//				dynamicDefVal);
//		boolean isObstacle = booleanValue(SimObjectProperty.IS_OBSTACLE,
//				obstacleDefVal);
//
//		MovementCapabilititesImpl movementCap = new MovementCapabilititesImpl();
//		movementCap.setCanMove(isDynamic);
//		movementCap.setCanMove(isObstacle);
//		// TODO mass & stuff
//		entity.set(new SimplePhysicsComponent());
//		entity.set(movementCap);
//
//		/*
//		 * If has a name, create a nameComponent
//		 */
//		NameComponent nameC = createNameComponent(s);
//		if (nameC != null)
//		{
//			entity.set(nameC);
//		}
//		/*
//		 * If it is a teleport, add a teleport component
//		 */
//		if (isTeleport(s))
//		{
//			String teleportName = getTeleportName(s);
//			TeleportComponentImpl originTeleport = new TeleportComponentImpl(
//					teleportName, getTeleportType(s));
//			entity.set(originTeleport);
//			if (this.teleportMap.containsKey(teleportName))
//			{
//				originTeleport.setTarget(this.teleportMap.get(teleportName));
//			} else
//			{
//				this.teleportMap.put(teleportName, entity);
//			}
//
//		}
//		return entity;
//	}
//
//	private Floor getFloor(Selectable s)
//	{
//		if (s instanceof Elevatable)
//		{
//			return this.getFloorMap().get(((Elevatable) s).getLevel());
//		} else
//		{
//			return null;
//		}
//	}
//
//	private Map<Level, Floor> getFloorMap()
//	{
//		if (this.floorMap == null)
//		{
//			this.floorMap = createFloorMap(this.home);
//		}
//		return this.floorMap;
//	}
//
//	private static Map<Level, Floor> createFloorMap(Home home)
//	{
//		/*
//		 * Maps linking the sweethome3d level with the rooms, furniture and
//		 * walls in it
//		 */
//		final HashMap<Level, ArrayList<Room>> levelRooms = getLevelsElevatables(
//				home.getRooms());
//		final HashMap<Level, ArrayList<Wall>> levelWalls = getLevelsElevatables(
//				home.getWalls());
//		final HashMap<Level, ArrayList<HomePieceOfFurniture>> levelFurniture = getLevelsElevatables(
//				home.getFurniture());
//		HashMap<Level, Floor> levelsFloors = new HashMap<>();
//
//		final ArrayList<Level> levels = new ArrayList<>(home.getLevels());
//		if (levels.isEmpty())
//		{
//			levels.add(null);
//		}
//		for (final Level level : levels)
//		{
//			if (!levelRooms.containsKey(level))
//			{
//				levelRooms.put(level, new ArrayList<Room>());
//			}
//			if (!levelWalls.containsKey(level))
//			{
//				levelWalls.put(level, new ArrayList<Wall>());
//			}
//			if (!levelFurniture.containsKey(level))
//			{
//				levelFurniture.put(level,
//						new ArrayList<HomePieceOfFurniture>());
//			}
//			String fname = level.getName();
//			fname = fname == null ? "NONAME" : fname;
//			final Floor f = new FloorImpl(fname);
//			levelsFloors.put(level, f);
//		}
//		return levelsFloors;
//	}
//
//	private static long newUUID()
//	{
//		return ID_GEN.getAndIncrement();
//	}
//
//	private static boolean booleanValue(String s)
//	{
//		return (s != null && "true".equals(s.toLowerCase()));
//	}
//
//	private static boolean booleanValue(Enum<?> s, boolean defVal)
//	{
//		return booleanValue(s.toString(), defVal);
//	}
//
//	private static boolean isNullOrEmpty(String s)
//	{
//		return "".equals(s) || s == null;
//	}
//
//	private static boolean booleanValue(String s, boolean defVal)
//	{
//		if ("true".equals(s))
//			return true;
//		return defVal;
//	}
//
//	private String getMetadata(Selectable s, String key)
//	{
//		return this.metadataManager.getMetadata(s).get(key);
//	}
//
//	private String getMetadata(Selectable s, Enum<?> key)
//	{
//		return getMetadata(s, key.toString());
//	}
//
//	private NameComponent createNameComponent(Selectable s)
//	{
//		String name = getName(s);
//		if (!"".equals(name) && name != null)
//		{
//			return new NameComponentImpl(name);
//		}
//		return null;
//	}
//
//	private boolean isTeleport(Selectable s)
//	{
//		return getTeleportName(s) != null;
//	}
//
//	private TeleportType getTeleportType(Selectable s)
//	{
//		if (!isTeleport(s))
//			return null;
//		TeleportType type = getMetadata(s, SimObjectProperty.TYPE.toString())
//				.equalsIgnoreCase(SimObjectProperty.START.toString())
//						? TeleportType.START
//						: TeleportType.END;
//		return type;
//	}
//
//	private String getTeleportName(Selectable s)
//	{
//		return getMetadata(s, SimObjectProperty.TELEPORT);
//	}
//
//	private String getTargetTeleportName(Selectable s)
//	{
//		return getMetadata(s, SimObjectProperty.TELEPORT);
//	}
//
//	private String getName(Selectable s)
//	{
//		String name = null;
//		if (s instanceof HomePieceOfFurniture)
//		{
//			name = ((HomePieceOfFurniture) s).getName();
//		}
//		if (name == null)
//			if (s instanceof Room)
//			{
//				name = ((Room) s).getName();
//			}
//		if (name == null)
//		{
//			name = this.getMetadata(s, SimObjectProperty.NAME.toString());
//		}
//		if (name == null)
//		{
//			name = this.getMetadata(s,
//					SimObjectProperty.POINT_OF_INTEREST.toString());
//		}
//		return name;
//	}
//
//}

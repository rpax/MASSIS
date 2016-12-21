package com.massisframework.massis.model.components.building;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomeDoorOrWindow;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.model.Selectable;
import com.eteks.sweethome3d.model.Wall;
import com.massisframework.massis.model.components.NameComponent;
import com.massisframework.massis.model.components.TeleportComponent.TeleportType;
import com.massisframework.massis.model.components.building.impl.Coordinate2DComponentImpl;
import com.massisframework.massis.model.components.building.impl.DefaultSimulationEntity;
import com.massisframework.massis.model.components.building.impl.HashMetadataComponent;
import com.massisframework.massis.model.components.building.impl.KPolygonShapeComponent;
import com.massisframework.massis.model.components.building.impl.MovementCapabilititesImpl;
import com.massisframework.massis.model.components.building.impl.RoomComponentImpl;
import com.massisframework.massis.model.components.building.impl.SimplePhysicsComponent;
import com.massisframework.massis.model.components.building.impl.TeleportComponentImpl;
import com.massisframework.massis.model.components.building.impl.WallComponentImpl;
import com.massisframework.massis.sim.SimulationEntity;
import com.massisframework.massis.util.SimObjectProperty;
import com.massisframework.sweethome3d.metadata.BuildingMetadataManager;
import com.massisframework.sweethome3d.metadata.HomeMetadataLoader;

public class SimulationEntityFactory {

	private static final AtomicLong ID_GEN = new AtomicLong();
	private static Map<Home, SimulationEntityFactory> factories;
	//
	private Home home;
	private BuildingMetadataManager metadataManager;

	private Map<String, SimulationEntity> teleportMap;

	private SimulationEntityFactory(Home home)
	{
		this.home = home;
		this.metadataManager = HomeMetadataLoader
				.getBuildingMetadataManager(this.home);
		this.teleportMap = new HashMap<>();
	}

	public synchronized static SimulationEntityFactory get(Home home)
	{

		if (factories == null)
		{
			factories = new HashMap<>();
		}
		SimulationEntityFactory factory = factories.get(home);
		if (factory == null)
		{
			factory = new SimulationEntityFactory(home);
			factories.put(home, factory);
		}

		return factory;
	}

	public SimulationEntity createEntity()
	{
		return new DefaultSimulationEntity(newUUID());
	}

	public SimulationEntity createWall(Wall wall)
	{
		SimulationEntity entity = createHomeEntity(wall);
		entity.set(new WallComponentImpl());
		return entity;
	}

	public SimulationEntity createFurniture(HomePieceOfFurniture f)
	{
		SimulationEntity entity = createHomeEntity(f);
		// 1 add room component
		entity.set(new RoomComponentImpl());
		// 2. It is a teleport?
		return entity;
	}

	public SimulationEntity createRoom(Room room)
	{
		// 1 entity with default components
		SimulationEntity entity = createHomeEntity(room);
		return entity;
	}

	private SimulationEntity createHomeEntity(Selectable s)
	{
		SimulationEntity entity = createEntity();
		// metadata
		MetadataComponent mc = new HashMetadataComponent(
				this.metadataManager.getMetadata(s));
		entity.set(mc);
		// Shape
		KPolygonShapeComponent psc = new KPolygonShapeComponent(s.getPoints());
		entity.set(psc);
		// Coordinates
		Coordinate2DComponentImpl coord = new Coordinate2DComponentImpl();
		coord.setX(psc.getPolygon().getCenter().x);
		coord.setY(psc.getPolygon().getCenter().y);
		entity.set(coord);
		//
		boolean dynamicDefVal = false;
		boolean obstacleDefVal = true;
		if (s instanceof Room)
		{
			obstacleDefVal = false;
		}
		if (s instanceof HomeDoorOrWindow)
		{
			obstacleDefVal = false;
		}

		boolean isDynamic = booleanValue(SimObjectProperty.IS_DYNAMIC,
				dynamicDefVal);
		boolean isObstacle = booleanValue(SimObjectProperty.IS_OBSTACLE,
				obstacleDefVal);

		MovementCapabilititesImpl movementCap = new MovementCapabilititesImpl();
		movementCap.setCanMove(isDynamic);
		movementCap.setCanMove(isObstacle);
		// TODO mass & stuff
		entity.set(new SimplePhysicsComponent());
		entity.set(movementCap);

		/*
		 * If has a name, create a nameComponent
		 */
		NameComponent nameC = createNameComponent(s);
		if (nameC != null)
		{
			entity.set(nameC);
		}
		/*
		 * If it is a teleport, add a teleport component
		 */
		if (isTeleport(s))
		{
			this.teleportMap.put(getTeleportName(s), entity);
			TeleportComponentImpl teleport=new TeleportComponentImpl(this.teleportMap);
			teleport.setTargetTeleportName(getTargetTeleportName(s));
			teleport.setTeleportType(getTeleportType(s));
		}
		return entity;
	}

	private static long newUUID()
	{
		return ID_GEN.getAndIncrement();
	}

	private static boolean booleanValue(String s)
	{
		return (s != null && "true".equals(s.toLowerCase()));
	}

	private static boolean booleanValue(Enum<?> s, boolean defVal)
	{
		return booleanValue(s.toString(), defVal);
	}

	private static boolean isNullOrEmpty(String s)
	{
		return "".equals(s) || s == null;
	}

	private static boolean booleanValue(String s, boolean defVal)
	{
		if ("true".equals(s))
			return true;
		return defVal;
	}

	private String getMetadata(Selectable s, String key)
	{
		return this.metadataManager.getMetadata(s).get(key);
	}

	private String getMetadata(Selectable s, Enum<?> key)
	{
		return getMetadata(s, key.toString());
	}

	private NameComponent createNameComponent(Selectable s)
	{
		String name = getName(s);
		if (!"".equals(name) && name != null)
		{
			return new NameComponentImpl(name);
		}
		return null;
	}

	private boolean isTeleport(Selectable s)
	{
		return getTeleportName(s) != null;
	}

	private TeleportType getTeleportType(Selectable s)
	{
		if (!isTeleport(s))
			return null;
		TeleportType type = getMetadata(s, SimObjectProperty.TYPE.toString())
				.equalsIgnoreCase(SimObjectProperty.START.toString())
						? TeleportType.START
						: TeleportType.END;
		return type;
	}

	private String getTeleportName(Selectable s)
	{
		return getMetadata(s, SimObjectProperty.TELEPORT);
	}
	private String getTargetTeleportName(Selectable s)
	{
		return getMetadata(s, SimObjectProperty.TELEPORT);
	}

	private String getName(Selectable s)
	{
		String name = null;
		if (s instanceof HomePieceOfFurniture)
		{
			name = ((HomePieceOfFurniture) s).getName();
		}
		if (name == null)
			if (s instanceof Room)
			{
				name = ((Room) s).getName();
			}
		if (name == null)
		{
			name = this.getMetadata(s, SimObjectProperty.NAME.toString());
		}
		return name;
	}

}

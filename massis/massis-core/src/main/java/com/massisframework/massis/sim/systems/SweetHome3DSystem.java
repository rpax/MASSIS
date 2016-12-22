package com.massisframework.massis.sim.systems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.eteks.sweethome3d.model.Elevatable;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomeDoorOrWindow;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Level;
import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.model.Selectable;
import com.eteks.sweethome3d.model.Wall;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Inject;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.components.Location;
import com.massisframework.massis.model.components.NameComponent;
import com.massisframework.massis.model.components.building.MetadataComponent;
import com.massisframework.massis.model.components.building.ShapeComponent;
import com.massisframework.massis.model.components.building.impl.MovementCapabilititesImpl;
import com.massisframework.massis.model.components.building.impl.SimplePhysicsComponent;
import com.massisframework.massis.sim.SimulationEntity;
import com.massisframework.massis.sim.engine.SimulationEngine;
import com.massisframework.massis.sim.engine.SimulationSystem;
import com.massisframework.massis.util.SH3DUtils;
import com.massisframework.massis.util.SimObjectProperty;
import com.massisframework.sweethome3d.metadata.BuildingMetadataManager;
import com.massisframework.sweethome3d.metadata.HomeMetadataLoader;

import straightedge.geom.KPoint;
@SuppressWarnings("unused")
public class SweetHome3DSystem implements SimulationSystem {

	private Home home;
	private BuildingMetadataManager metadataManager;
	private BiMap<Level, SimulationEntity> levelsFloors;
	private SimulationEngine engine;

	@Inject
	public SweetHome3DSystem(Home home)
	{
		this.home = home;
		this.metadataManager = HomeMetadataLoader
				.getBuildingMetadataManager(home);

	}

	@Override
	public void addedToEngine(SimulationEngine simEngine)
	{
		this.engine = simEngine;
		this.createFloorMap();
	}

	@Override
	public void update(float deltaTime)
	{

	}

	@Override
	public void removedFromEngine(SimulationEngine simEngine)
	{
		// uuh
	}

	private SimulationEntity createHomeEntity(Selectable s)
	{
		SimulationEntity entity = this.engine.createEntity();
		// metadata

		MetadataComponent mc = this.engine
				.newComponent(MetadataComponent.class);
		mc.putAll(this.metadataManager.getMetadata(s));
		entity.set(mc);
		// Shape
		ShapeComponent psc = this.engine.newComponent(ShapeComponent.class);
		psc.setShape(s.getPoints());
		entity.set(psc);
		// Coordinates
		Location coord = this.engine.newComponent(Location.class);
		// TODO napa
		KPoint center = SH3DUtils.createKPolygonFromSH3DObj(s).getCenter();
		coord.setX(center.x);
		coord.setY(center.y);
		coord.setFloor(getFloor(s).get(Floor.class));
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

		return entity;
	}

	private SimulationEntity getFloor(Selectable s)
	{
		if (s instanceof Elevatable)
		{
			return this.levelsFloors.get(((Elevatable) s).getLevel());
		} else
		{
			return null;
		}
	}

	private void createFloorMap()
	{
		this.levelsFloors = HashBiMap.create();
		/*
		 * Maps linking the sweethome3d level with the rooms, furniture and
		 * walls in it
		 */
		final HashMap<Level, ArrayList<Room>> levelRooms = getLevelsElevatables(
				home.getRooms());
		final HashMap<Level, ArrayList<Wall>> levelWalls = getLevelsElevatables(
				home.getWalls());
		final HashMap<Level, ArrayList<HomePieceOfFurniture>> levelFurniture = getLevelsElevatables(
				home.getFurniture());

		final ArrayList<Level> levels = new ArrayList<>(home.getLevels());
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
				levelFurniture.put(level,
						new ArrayList<HomePieceOfFurniture>());
			}
			String fname = level.getName();
			fname = fname == null ? "NONAME" : fname;
			final Floor f = this.engine.newComponent(Floor.class);
			SimulationEntity e = this.engine.createEntity();
			e.set(f);
			levelsFloors.put(level, e);
		}
	}

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
		NameComponent nameC = null;
		if (!"".equals(name) && name != null)
		{
			nameC = engine.newComponent(NameComponent.class);
			nameC.setName(name);
		}
		return nameC;
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
		if (name == null)
		{
			name = this.getMetadata(s,
					SimObjectProperty.POINT_OF_INTEREST.toString());
		}
		return name;
	}

}

package com.massisframework.massis.model.components.building;

import java.util.concurrent.atomic.AtomicLong;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Selectable;
import com.eteks.sweethome3d.model.Wall;
import com.massisframework.massis.model.components.building.impl.Coordinate2DComponentImpl;
import com.massisframework.massis.model.components.building.impl.DefaultSimulationEntity;
import com.massisframework.massis.model.components.building.impl.HashMetadataComponent;
import com.massisframework.massis.model.components.building.impl.KPolygonShapeComponent;
import com.massisframework.massis.model.components.building.impl.MovementCapabilititesImpl;
import com.massisframework.massis.model.components.building.impl.SimplePhysicsComponent;
import com.massisframework.massis.model.components.building.impl.WallComponentImpl;
import com.massisframework.massis.sim.SimulationEntity;
import com.massisframework.massis.util.SimObjectProperty;
import com.massisframework.sweethome3d.metadata.BuildingMetadataManager;
import com.massisframework.sweethome3d.metadata.HomeMetadataLoader;

public class SimulationEntityFactory {

	private Home home;
	private BuildingMetadataManager metadataManager;
	private static final AtomicLong ID_GEN = new AtomicLong();

	public SimulationEntityFactory(Home home)
	{
		this.home = home;
		this.metadataManager = HomeMetadataLoader
				.getBuildingMetadataManager(this.home);
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
		//1
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
		MovementCapabilititesImpl movementCap = new MovementCapabilititesImpl();
		movementCap.setCanMove(
				booleanValue(mc.get(SimObjectProperty.IS_DYNAMIC.toString())));
		movementCap.setCanMove(
				booleanValue(mc.get(SimObjectProperty.IS_OBSTACLE.toString())));
		// TODO mass & stuff
		entity.set(new SimplePhysicsComponent());
		entity.set(movementCap);
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

}

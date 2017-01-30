package com.massisframework.massis.model.systems.sh3d;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.eteks.sweethome3d.io.HomeFileRecorder;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomeDoorOrWindow;
import com.eteks.sweethome3d.model.HomeObject;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Level;
import com.eteks.sweethome3d.model.RecorderException;
import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.model.Selectable;
import com.eteks.sweethome3d.model.Wall;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.massisframework.massis.model.components.DoorComponent;
import com.massisframework.massis.model.components.FloorReference;
import com.massisframework.massis.model.components.Metadata;
import com.massisframework.massis.model.components.Position2D;
import com.massisframework.massis.model.components.RoomComponent;
import com.massisframework.massis.model.components.ShapeComponent;
import com.massisframework.massis.model.components.SteeringComponent;
import com.massisframework.massis.model.components.Velocity;
import com.massisframework.massis.model.components.VisionArea;
import com.massisframework.massis.model.components.WallComponent;
import com.massisframework.massis.model.components.WindowComponent;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.injection.SimulationConfiguration;
import com.massisframework.massis.util.SH3DUtils;
import com.massisframework.massis.util.SimObjectProperty;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

public class BuildingSystem implements SimulationSystem {

	@Inject
	SimulationEngine engine;
	@Inject
	SimulationConfiguration configuration;
	private Home home;

	@Override
	public void initialize()
	{
		File buildingFile = this.configuration.getBuildingFile();

		try
		{
			this.home = new HomeFileRecorder()
					.readHome(buildingFile.getAbsolutePath());
			if (home.getLevels().isEmpty())
			{
				this.createLevel(null);
			}
			home.getLevels().forEach(this::createLevel);
			home.getWalls().forEach(this::createWall);
			home.getRooms().forEach(this::createRoom);
			home.getFurniture().forEach(this::createFurniture);

		} catch (RecorderException e)
		{
			e.printStackTrace();
		}
	}

	private void createLevel(Level lvl)
	{
		int floorId = this.engine.createEntity();
		SimulationEntity floorEntity = this.engine.asSimulationEntity(floorId);
		floorEntity.addComponent(SweetHome3DLevel.class)
				.setLevel(lvl);
		this.home.getWalls()
				.stream()
				.filter(w -> w.getLevel() == lvl)
				.forEach(w -> {
					createEntity(floorId, w).addComponent(WallComponent.class);
				});

		this.home.getRooms()
				.stream()
				.filter(w -> w.getLevel() == lvl)
				.forEach(w -> {
					createEntity(floorId, w).addComponent(RoomComponent.class);
				});
		this.home.getFurniture()
				.stream()
				.filter(w -> w.getLevel() == lvl)
				.forEach(w -> {
					createFurnitureComponent(floorId, w);
				});
	}

	private void createFurnitureComponent(int floorId, HomePieceOfFurniture f)
	{
		SimulationEntity e = createEntity(floorId, f);
		if (f instanceof HomeDoorOrWindow)
		{
			// Comprobar si es ventana o no
			if (f.getName() != null
					&& f.getName().toUpperCase().contains(
							SimObjectProperty.WINDOW.toString()))
			{
				e.addComponent(WindowComponent.class);

			} else
			{
				e.addComponent(DoorComponent.class);
			}
		} else
		{
			e.addComponent(Velocity.class);
			e.addComponent(SteeringComponent.class);
			e.addComponent(VisionArea.class);
			// TODO falta meter la IA
		}

	}

	private void createWall(Wall lvl)
	{
		int eid = this.engine.createEntity();
		this.engine.asSimulationEntity(eid).addComponent(SweetHome3DWall.class)
				.setWall(lvl);
	}

	private void createRoom(Room lvl)
	{
		int eid = this.engine.createEntity();
		this.engine.asSimulationEntity(eid).addComponent(SweetHome3DRoom.class)
				.setRoom(lvl);
	}

	private void createFurniture(HomePieceOfFurniture lvl)
	{
		int eid = this.engine.createEntity();
		this.engine.asSimulationEntity(eid)
				.addComponent(SweetHome3DFurniture.class)
				.setFurniture(lvl);
	}

	@Override
	public void update(float deltaTime)
	{

	}

	private SimulationEntity createEntity(int floorId, Selectable w)
	{
		KPolygon shape = SH3DUtils.createKPolygonFromSH3DObj(w);
		KPoint center = shape.getCenter();
		int entityId = engine.createEntity();
		SimulationEntity e = engine.asSimulationEntity(entityId);
		e.addComponent(FloorReference.class).setFloorId(floorId);
		e.addComponent(Metadata.class).set(getMetadata((HomeObject) w));
		e.addComponent(Position2D.class).set(center.x, center.y);
		e.addComponent(ShapeComponent.class).setShape(shape);
		return e;
	}

	private Map<String, String> getMetadata(HomeObject f)
	{
		// TODO temporary
		Map<String, String> metadata = new HashMap<>();
		JsonObject[] json = new Gson().fromJson(
				f.getProperty("__MASSIS_METADATA_v1000"), JsonObject[].class);
		if (json != null)
		{

			for (int i = 0; i < json.length; i++)
			{
				json[i].get("attributes").getAsJsonArray().forEach(item -> {
					metadata.put(
							item.getAsJsonObject().get("key").getAsString(),
							item.getAsJsonObject().get("value").getAsString());
				});
			}
		}
		return metadata;
	}
}

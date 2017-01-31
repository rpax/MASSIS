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
import com.eteks.sweethome3d.model.Selectable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.massisframework.massis.model.components.DoorComponent;
import com.massisframework.massis.model.components.DynamicObstacle;
import com.massisframework.massis.model.components.EntityRangeFinder;
import com.massisframework.massis.model.components.Floor;
import com.massisframework.massis.model.components.FloorReference;
import com.massisframework.massis.model.components.Metadata;
import com.massisframework.massis.model.components.NameComponent;
import com.massisframework.massis.model.components.Orientation;
import com.massisframework.massis.model.components.Position2D;
import com.massisframework.massis.model.components.RenderComponent;
import com.massisframework.massis.model.components.RoomComponent;
import com.massisframework.massis.model.components.Velocity;
import com.massisframework.massis.model.components.VisionArea;
import com.massisframework.massis.model.components.WallComponent;
import com.massisframework.massis.model.components.WindowComponent;
import com.massisframework.massis.model.components.impl.ShapeComponentImpl;
import com.massisframework.massis.model.systems.rendering.renderers.AgentArrowRenderer;
import com.massisframework.massis.model.systems.rendering.renderers.DoorRenderer;
import com.massisframework.massis.model.systems.rendering.renderers.RoomRenderer;
import com.massisframework.massis.model.systems.rendering.renderers.WallRenderer;
import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.injection.SimulationConfiguration;
import com.massisframework.massis.util.SH3DUtils;
import com.massisframework.massis.util.SimObjectProperty;

import javafx.scene.paint.Color;
import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

public class BuildingSystem implements SimulationSystem {

	@Inject
	SimulationEngine<?> engine;
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

		} catch (RecorderException e)
		{
			e.printStackTrace();
		}
	}

	private void createLevel(Level lvl)
	{
		int floorId = this.engine.createEntity();
		SimulationEntity<?> floorEntity = this.engine
				.asSimulationEntity(floorId);
		floorEntity.addComponent(SweetHome3DLevel.class)
				.setLevel(lvl);
		floorEntity.addComponent(Floor.class);
		String floorName = "NONAME";
		if (lvl != null && lvl.getName() != null)
		{
			floorName = lvl.getName();
		}
		floorEntity.addComponent(NameComponent.class).set(floorName);

		this.home.getWalls()
				.stream()
				.filter(w -> w.getLevel() == lvl)
				.forEach(w -> {
					SimulationEntity<?> wallEntity = createEntity(floorId, w);
					wallEntity.addComponent(WallComponent.class);
					wallEntity.addComponent(RenderComponent.class)
							.setRenderer(WallRenderer.renderer);

				});

		this.home.getRooms()
				.stream()
				.filter(w -> w.getLevel() == lvl)
				.forEach(w -> {
					SimulationEntity<?> roomEntity = createEntity(floorId, w);
					roomEntity.addComponent(RoomComponent.class);
					roomEntity.addComponent(RenderComponent.class)
							.setRenderer(RoomRenderer.renderer);

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
		SimulationEntity<?> e = createEntity(floorId, f);
		e.addComponent(SweetHome3DFurniture.class).setFurniture(f);
		e.addComponent(Orientation.class).setAngle(f.getAngle());
		if (f instanceof HomeDoorOrWindow)
		{
			// Comprobar si es ventana o no
			if (isWindow(f))
			{
				e.addComponent(WindowComponent.class);

			} else
			{
				e.addComponent(DoorComponent.class);
				e.addComponent(RenderComponent.class).setRenderer(DoorRenderer.renderer);
			}
		} else
		{

			e.addComponent(Velocity.class);
			e.addComponent(VisionArea.class);
			e.addComponent(EntityRangeFinder.class);
			e.addComponent(RenderComponent.class).setRenderer(AgentArrowRenderer.renderer);
			String className = getMetadata(f)
					.get(SimObjectProperty.CLASSNAME.toString());
			if (className != null)
			{
				e.addComponent(DynamicObstacle.class);
				try
				{
					e.addComponent((Class<? extends SimulationComponent>) Class
							.forName(className));
				} catch (ClassNotFoundException e1)
				{
					throw new RuntimeException(e1);
				}
			} else
			{

			}
		}

	}

	private boolean isWindow(HomePieceOfFurniture f)
	{
		return f.getName() != null
				&& f.getName().toUpperCase().contains(
						SimObjectProperty.WINDOW.toString());
	}

	@Override
	public void update(float deltaTime)
	{

	}

	private SimulationEntity<?> createEntity(int floorId, Selectable w)
	{
		KPolygon shape = SH3DUtils.createKPolygonFromSH3DObj(w);
		KPoint center = shape.getCenter();
		int entityId = engine.createEntity();
		SimulationEntity<?> e = engine.asSimulationEntity(entityId);
		e.addComponent(FloorReference.class).setFloorId(floorId);
		e.addComponent(Metadata.class).set(getMetadata((HomeObject) w));
		e.addComponent(Position2D.class).set(center.x, center.y);
		e.addComponent(ShapeComponentImpl.class).setShape(shape);
		e.addComponent(Orientation.class);

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

	public Home getHome()
	{
		return home;
	}

}

package com.massisframework.massis.model.systems.sh3d;

import java.util.HashMap;
import java.util.Map;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomeDoorOrWindow;
import com.eteks.sweethome3d.model.HomeObject;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Level;
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
import com.massisframework.massis.model.components.RenderComponent;
import com.massisframework.massis.model.components.RoomComponent;
import com.massisframework.massis.model.components.TransformComponent;
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
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationEntityData;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.util.SH3DUtils;
import com.massisframework.massis.util.SimObjectProperty;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

public class BuildingSystem implements SimulationSystem {

	private Home home;

	@Inject
	public BuildingSystem(Home home)
	{
		this.home = home;
	}

	@Inject
	private SimulationEntityData ed;

	@Override
	public void initialize()
	{
		// File buildingFile = this.configuration.getBuildingFile();
		//
		// try
		// {
		// this.home = new HomeFileRecorder()
		// .readHome(buildingFile.getAbsolutePath());
		//
		//
		// } catch (RecorderException e)
		// {
		// e.printStackTrace();
		// }
		if (home.getLevels().isEmpty())
		{
			this.createLevel(null);
		}
		home.getLevels().forEach(this::createLevel);
	}

	private void createLevel(Level lvl)
	{
		SimulationEntity floorEntity = this.ed.createEntity();

		floorEntity
				.add(SweetHome3DLevel.class)
				.set(SweetHome3DLevel::setLevel, lvl);
		// TODO max min...etc
		floorEntity.add(Floor.class);

		String floorName = "NONAME";
		if (lvl != null && lvl.getName() != null)
		{
			floorName = lvl.getName();
		}
		floorEntity
				.add(NameComponent.class)
				.set(NameComponent::set, floorName);

		this.home.getWalls()
				.stream()
				.filter(w -> w.getLevel() == lvl)
				.forEach(w -> {
					SimulationEntity wallEntity = createEntity(
							floorEntity.id(), w);
					wallEntity.add(WallComponent.class);
					wallEntity.add(RenderComponent.class).set(
							RenderComponent::setRenderer,
							WallRenderer.renderer);

				});

		this.home.getRooms()
				.stream()
				.filter(w -> w.getLevel() == lvl)
				.forEach(w -> {
					SimulationEntity roomEntity = createEntity(
							floorEntity.id(),
							w);
					//

					roomEntity.add(RoomComponent.class);
					roomEntity.add(RenderComponent.class)
							.set(RenderComponent::setRenderer,
									RoomRenderer.renderer);

				});
		this.home.getFurniture()
				.stream()
				.filter(w -> w.getLevel() == lvl)
				.forEach(w -> {
					createFurnitureComponent(floorEntity.id(), w);
				});
	}

	private void createFurnitureComponent(long floorId, HomePieceOfFurniture f)
	{
		SimulationEntity e = createEntity(floorId, f);
		e.add(SweetHome3DFurniture.class)
				.set(SweetHome3DFurniture::setFurniture, f);
		e.edit(TransformComponent.class)
				.set(TransformComponent::setAngle, f.getAngle());
		if (f instanceof HomeDoorOrWindow)
		{
			// Comprobar si es ventana o no
			if (isWindow(f))
			{
				e.add(WindowComponent.class);

			} else
			{
				e
						.add(DoorComponent.class)
						.add(RenderComponent.class)
						.set(RenderComponent::setRenderer,
								DoorRenderer.renderer);

			}
		} else
		{

			e.add(Velocity.class);
			e.add(VisionArea.class);
			e.add(EntityRangeFinder.class);
			e.add(RenderComponent.class)
					.set(RenderComponent::setRenderer,
							AgentArrowRenderer.renderer);
			String className = getMetadata(f)
					.get(SimObjectProperty.CLASSNAME.toString());
			if (className != null)
			{
				e.add(DynamicObstacle.class);
				try
				{
					e.add((Class<? extends SimulationComponent>) Class
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

	private SimulationEntity createEntity(long floorId, Selectable w)
	{
		KPolygon shape = SH3DUtils.createKPolygonFromSH3DObj(w);
		KPoint center = shape.getCenter();
		SimulationEntity entity = this.ed.createEntity();

		entity.add(FloorReference.class).set(FloorReference::setFloorId,
				floorId);
		entity.add(Metadata.class).set(Metadata::set,
				getMetadata((HomeObject) w));

		entity.add(TransformComponent.class)
				.set(TransformComponent::setX, center.x)
				.set(TransformComponent::setY, center.y);

		entity.add(ShapeComponentImpl.class)
				.set(ShapeComponentImpl::setShape, shape);

		return entity;
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

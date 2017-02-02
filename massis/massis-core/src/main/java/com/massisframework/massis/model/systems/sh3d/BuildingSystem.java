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
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntity;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntityData;
import com.massisframework.massis.util.SH3DUtils;
import com.massisframework.massis.util.SimObjectProperty;
import com.simsilica.es.EntityId;

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
		EntityId floorId = this.ed.createEntity();

		ed.add(floorId, SweetHome3DLevel.class)
				.set(SweetHome3DLevel::setLevel, lvl)
				.commit();
		ed.add(floorId, Floor.class).commit();
		String floorName = "NONAME";
		if (lvl != null && lvl.getName() != null)
		{
			floorName = lvl.getName();
		}
		ed
				.add(floorId, NameComponent.class)
				.set(NameComponent::set, floorName)
				.commit();

		this.home.getWalls()
				.stream()
				.filter(w -> w.getLevel() == lvl)
				.forEach(w -> {
					SimulationEntity wallEntity = createEntity(floorId.getId(),
							w);
					wallEntity.addC(WallComponent.class).commit();
					wallEntity.addC(RenderComponent.class)
							.set(RenderComponent::setRenderer,
									WallRenderer.renderer)
							.commit();

				});

		this.home.getRooms()
				.stream()
				.filter(w -> w.getLevel() == lvl)
				.forEach(w -> {
					SimulationEntity roomEntity = createEntity(floorId.getId(),
							w);
					//

					roomEntity.addC(RoomComponent.class).commit()
							.addC(RenderComponent.class)
							.set(RenderComponent::setRenderer,
									RoomRenderer.renderer)
							.commit();

				});
		this.home.getFurniture()
				.stream()
				.filter(w -> w.getLevel() == lvl)
				.forEach(w -> {
					createFurnitureComponent(floorId.getId(), w);
				});
	}

	private void createFurnitureComponent(long floorId, HomePieceOfFurniture f)
	{
		SimulationEntity e = createEntity(floorId, f);
		e.addC(SweetHome3DFurniture.class)
				.set(SweetHome3DFurniture::setFurniture, f)
				.commit();
		e.editC(TransformComponent.class)
				.set(TransformComponent::setAngle, f.getAngle())
				.commit();
		if (f instanceof HomeDoorOrWindow)
		{
			// Comprobar si es ventana o no
			if (isWindow(f))
			{
				e.addC(WindowComponent.class).commit();

			} else
			{
				e.addC(DoorComponent.class).commit();
				e.addC(RenderComponent.class)
						.set(RenderComponent::setRenderer,
								DoorRenderer.renderer)
						.commit();
			}
		} else
		{

			e.addC(Velocity.class).commit();
			e.addC(VisionArea.class).commit();
			e.addC(EntityRangeFinder.class).commit();
			e.addC(RenderComponent.class)
					.set(RenderComponent::setRenderer,
							AgentArrowRenderer.renderer)
					.commit();
			String className = getMetadata(f)
					.get(SimObjectProperty.CLASSNAME.toString());
			if (className != null)
			{
				e.addC(DynamicObstacle.class).commit();
				try
				{
					e.addC((Class<? extends SimulationComponent>) Class
							.forName(className)).commit();
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
		EntityId entityId = this.ed.createEntity();

		ed.addGet(entityId, FloorReference.class).setFloorId(floorId);
		ed.addGet(entityId, Metadata.class).set(getMetadata((HomeObject) w));
		ed.get(entityId, TransformComponent.class)
				.setX((float) center.x).setY((float) center.y);
		ed.add(entityId, ShapeComponentImpl.class)
				.set(ShapeComponentImpl::setShape, shape)
				.commit();

		return ed.getSimulationEntity(entityId);
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

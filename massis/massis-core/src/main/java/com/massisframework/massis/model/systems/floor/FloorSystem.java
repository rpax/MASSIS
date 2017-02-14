package com.massisframework.massis.model.systems.floor;

import static com.massisframework.massis.sim.ecs.CollectionsFactory.newList;
import static com.massisframework.massis.sim.ecs.CollectionsFactory.newMapList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomeFurnitureGroup;
import com.eteks.sweethome3d.model.HomeObject;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Level;
import com.eteks.sweethome3d.model.Selectable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.massisframework.massis.model.components.FloorReference;
import com.massisframework.massis.model.components.TransformComponent;
import com.massisframework.massis.model.components.impl.FloorReferenceImpl;
import com.massisframework.massis.model.components.impl.MetadataComponentImpl;
import com.massisframework.massis.model.components.impl.NameComponentImpl;
import com.massisframework.massis.model.components.impl.RoomComponentImpl;
import com.massisframework.massis.model.components.impl.ShapeComponentImpl;
import com.massisframework.massis.model.components.impl.TransformImpl;
import com.massisframework.massis.model.systems.sh3d.SweetHome3DFurniture;
import com.massisframework.massis.model.systems.sh3d.SweetHome3DLevel;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationEntityData;
import com.massisframework.massis.sim.ecs.SimulationEntitySet;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.util.SH3DUtils;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

public class FloorSystem implements SimulationSystem {

	private SimulationEntityData ed;
	private SimulationEntitySet floorReferences;
	private SimulationEntitySet floors;
	private Map<Long, List<Long>> floorEntities;
	private Home home;

	@Inject
	public FloorSystem(Home home, SimulationEntityData ed)
	{
		this.ed = ed;
		this.home = home;
		this.floorEntities = newMapList(Long.class, Long.class);
	}

	@Override
	public void initialize()
	{
		this.floorReferences = this.ed.createEntitySet(
				FloorReference.class,
				TransformComponent.class);
		this.floors = this.ed.createEntitySet(Level.class);
		this.floors.applyChanges();
		this.floors.forEach(this::addFloorComponent);
		this.recomputeBounds();
	}

	private void addFloorComponent(SimulationEntity e)
	{
		Level lvl = e.get(SweetHome3DLevel.class).getLevel();
		String floorName = "NONAME";
		if (lvl != null && lvl.getName() != null)
		{
			floorName = lvl.getName();
		}
		e.add(new NameComponentImpl()).set(floorName);
		e.add(new FloorImpl());
		this.home
				.getWalls()
				.stream()
				.filter(w -> w.getLevel() == lvl)
				.map(w -> createEntity(e.id(), w))
				.forEach(ent -> ent.add(new WallComponentImpl()));
		this.home
				.getRooms()
				.stream()
				.filter(w -> w.getLevel() == lvl)
				.map(w -> createEntity(e.id(), w))
				.forEach(ent -> ent.add(new RoomComponentImpl()));
		this.home
				.getFurniture()
				.stream()
				.flatMap(f -> allFurniture(f))
				.filter(w -> w.getLevel() == lvl)
				.forEach(w -> {
					SimulationEntity ent = createEntity(e.id(), w);
					ent.add(new SweetHome3DFurniture()).setFurniture(w);
				});

	}

	private Stream<HomePieceOfFurniture> allFurniture(HomePieceOfFurniture f)
	{
		if (f instanceof HomeFurnitureGroup)
		{
			return ((HomeFurnitureGroup) f).getAllFurniture()
					.stream();
		} else
		{
			return Stream.of(f);
		}
	}

	private SimulationEntity createEntity(long floorId, Selectable w)
	{
		KPolygon shape = SH3DUtils.createKPolygonFromSH3DObj(w);
		KPoint center = shape.getCenter();
		if (w instanceof HomePieceOfFurniture)
		{
			shape.rotate(-((HomePieceOfFurniture) w).getAngle());
		}
		SimulationEntity entity = this.ed.createEntity();
		entity.add(new FloorReferenceImpl()).setFloorId(floorId);
		entity.add(new MetadataComponentImpl())
				.set(getMetadata((HomeObject) w));
		entity.add(new TransformImpl()).setX(center.x).setY(center.y);
		entity.add(new ShapeComponentImpl()).setShape(shape);
		return entity;
	}

	@Override
	public void update(float deltaTime)
	{
		if (this.floorReferences.applyChanges())
		{
			this.recomputeBounds();
		}
	}

	public List<Long> getEntitiesInFloor(long floorId)
	{
		return Collections.unmodifiableList(this.floorEntities
				.getOrDefault(floorId, Collections.emptyList()));
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

	private void recomputeBounds()
	{
		for (SimulationEntity e : this.floors)
		{
			FloorImpl f = e.get(FloorImpl.class);
			f.setMaxX(Integer.MIN_VALUE);
			f.setMaxY(Integer.MIN_VALUE);
			f.setMinX(Integer.MAX_VALUE);
			f.setMinY(Integer.MAX_VALUE);

		}
		for (SimulationEntity e : floorReferences)
		{
			long fId = e.get(FloorReference.class).getFloorId();
			List<Long> entitiesInFloor = this.floorEntities.get(fId);

			if (entitiesInFloor == null)
			{
				entitiesInFloor = newList(Long.class);
				this.floorEntities.put(fId, entitiesInFloor);
			}
			TransformComponent t = e.get(TransformComponent.class);
			FloorImpl floor = ed.get(fId, FloorImpl.class);

			floor.setMaxX((int) Math.max(floor.getMaxX(), t.getX() + 1));
			floor.setMaxY((int) Math.max(floor.getMaxY(), t.getY() + 1));

			floor.setMinX((int) Math.min(floor.getMinX(), t.getX() - 1));
			floor.setMinY((int) Math.min(floor.getMinY(), t.getY() - 1));

			entitiesInFloor.add(e.id());
		}
	}

}

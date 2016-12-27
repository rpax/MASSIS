package com.massisframework.massis.ecs.system.sweethome3d;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.Entity;
import com.eteks.sweethome3d.model.Level;
import com.massisframework.massis.ecs.components.Floor;
import com.massisframework.massis.ecs.components.NameComponent;
import com.massisframework.massis.ecs.components.PolygonComponent;
import com.massisframework.massis.ecs.components.ReferencedFloor;

public class FloorSystem extends BaseEntitySystem {

	private Archetype floorArcheType;
	private Map<Level, Integer> homeLevels;

	public FloorSystem()
	{
		super(Aspect.all(
				SweetHome3DComponent.class,
				SweetHome3DLevelComponent.class,
				PolygonComponent.class));
		this.homeLevels = new HashMap<>();
	}

	@Override
	protected void initialize()
	{
		super.initialize();
		this.floorArcheType = this.createFloorArchetype();
	}

	@Override
	protected void inserted(int entityId)
	{
		super.inserted(entityId);
		Entity homeEntity = this.world.getEntity(entityId);
		Level level = this.world.getEntity(entityId)
				.getComponent(SweetHome3DLevelComponent.class)
				.getLevel();
		
		Integer floorEntitId = homeLevels.get(level);
		if (floorEntitId == null)
		{
			floorEntitId = this.world.create(this.floorArcheType);
			System.out.println("Floor created");
			String name = "NONAME";
			if (level != null && level.getName() != null)
			{
				name = level.getName();
			}

			world.getEntity(floorEntitId)
					.getComponent(NameComponent.class).set(name);
			world.getEntity(floorEntitId)
					.getComponent(SweetHome3DLevelComponent.class).set(level);
			this.homeLevels.put(level, floorEntitId);
		}
		Rectangle homeObjectBounds = homeEntity
				.getComponent(PolygonComponent.class).get()
				.getBounds();
		world.getEntity(floorEntitId)
				.getComponent(Floor.class)
				.expand(homeObjectBounds);
		
		homeEntity
				.getComponent(ReferencedFloor.class)
				.setFloorEntityId(floorEntitId);
	}

	private Archetype createFloorArchetype()
	{
		return new ArchetypeBuilder()
				// Depends on location and rotation
				.add(Floor.class)
				.add(SweetHome3DLevelComponent.class)
				.add(NameComponent.class)
				.build(this.world);
	}

	@Override
	protected void processSystem()
	{

	}

}

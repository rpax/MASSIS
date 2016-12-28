package com.massisframework.massis.ecs.system.location;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.Entity;
import com.artemis.link.EntityLinkManager;
import com.artemis.link.LinkListener;
import com.massisframework.massis.ecs.components.BuildingLocation;
import com.massisframework.massis.ecs.components.Floor;
import com.massisframework.massis.ecs.util.SimulationObjects;

public class LocationSystem extends BaseEntitySystem {
	// Floor -> Locations
	public LocationSystem()
	{
		super(Aspect.all(BuildingLocation.class));
	}

	@Override
	protected void initialize()
	{
		this.world.getSystem(EntityLinkManager.class)
				.register(BuildingLocation.class, "floorId",
						new LinkListener() {
							@Override
							public void onTargetDead(int sourceId,
									int deadTargetId)
							{

							}

							@Override
							public void onLinkEstablished(
									int sourceId,
									int targetId)
							{
								addLink(sourceId, targetId);
							}

							@Override
							public void onTargetChanged(int sourceId,
									int targetId,
									int oldTargetId)
							{
								removeLink(sourceId, oldTargetId);
								addLink(sourceId, targetId);
							}

							@Override
							public void onLinkKilled(int sourceId, int targetId)
							{

							}

						});
	}

	private void removeLink(int sourceId, int targetId)
	{
		Entity source = world.getEntity(sourceId);
		Entity target = world.getEntity(targetId);
		Floor floor = target.getComponent(Floor.class);

		if (SimulationObjects.isWall(source))
		{
			floor.removeWall(sourceId);
		} else if (SimulationObjects.isRoom(source))
		{
			floor.removeRoom(sourceId);
		} else if (SimulationObjects.isDynamic(source))
		{
			floor.removeDynamicEntity(sourceId);
		} else if (SimulationObjects
				.isFurniture(source))
		{
			floor.removeFurniture(sourceId);
		}

	}

	private void addLink(int sourceId, int targetId)
	{
		Entity source = world.getEntity(sourceId);
		Entity target = world.getEntity(targetId);
		Floor floor = target.getComponent(Floor.class);
		if (SimulationObjects.isWall(source))
		{
			floor.addWall(sourceId);
		} else if (SimulationObjects.isRoom(source))
		{
			floor.addRoom(sourceId);
		} else if (SimulationObjects.isDynamic(source))
		{
			floor.addDynamicEntity(sourceId);
		} else if (SimulationObjects
				.isFurniture(source))
		{
			floor.addFurniture(sourceId);
		}
	}

	@Override
	protected void processSystem()
	{

	}

}

package com.massisframework.massis.ecs.system.location;

import java.util.Map;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.link.EntityLinkManager;
import com.artemis.link.LinkListener;
import com.artemis.utils.IntBag;
import com.massisframework.massis.ecs.components.BuildingLocation;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class LocationSystem extends BaseEntitySystem {

	Map<Integer, IntBag> floorItems;

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
								removeFloor(deadTargetId);
							}

							@Override
							public void onLinkEstablished(
									int sourceId,
									int targetId)
							{
								addLink(sourceId, targetId);
							}

							@Override
							public void onTargetChanged(
									int sourceId,
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
		this.removeFloorItem(sourceId, targetId);
	}

	private void addLink(int sourceId, int targetId)
	{
		this.addFloorItem(sourceId, targetId);
	}

	@Override
	protected void processSystem()
	{

	}

	private static final IntBag EMPTY_BAG = new IntBag();

	private void addFloorItem(int floorId, int floorItemId)
	{
		IntBag items = this.getFloorItemsMap().get(floorId);
		if (items == null)
		{
			items = new IntBag();
			this.getFloorItemsMap().put(floorId, items);
		}
		if (!items.contains(floorItemId))
		{
			items.add(floorItemId);
		}
	}

	private boolean containsFloorItem(int floorId, int floorItemId)
	{
		return getItemsInFloor(floorId).contains(floorItemId);
	}

	private boolean removeFloorItem(int floorId, int floorItemId)
	{
		return getItemsInFloor(floorId).removeValue(floorItemId);
	}

	private void removeFloor(int floorId)
	{
		this.getFloorItemsMap().remove(floorId);
	}

	public IntBag getItemsInFloor(int floorId)
	{

		IntBag items = this.getFloorItemsMap().get(floorId);
		if (items == null)
		{
			return EMPTY_BAG;
		} else
		{
			return items;
		}

	}

	private Map<Integer, IntBag> getFloorItemsMap()
	{
		if (this.floorItems == null)
		{
			this.floorItems = new Int2ObjectOpenHashMap<IntBag>();
		}
		return this.floorItems;
	}

}

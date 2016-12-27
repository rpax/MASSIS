package com.massisframework.massis.ecs.system.graphics;

import java.util.HashMap;
import java.util.Map;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.massisframework.gui.DrawableLayer;
import com.massisframework.gui.DrawableZone;
import com.massisframework.massis.ecs.components.Floor;
import com.massisframework.massis.ecs.components.NameComponent;

public class FloorLayersSystem extends BaseEntitySystem {

	private Map<Integer, FloorDrawableZone> drawableZones;

	public FloorLayersSystem()
	{
		super(Aspect.all(Floor.class));
	}

	@Override
	protected void initialize()
	{
		super.initialize();
		this.drawableZones = new HashMap<Integer, FloorDrawableZone>();
		IntBag actives = subscription.getEntities();
		int[] ids = actives.getData();
		for (int i = 0, s = actives.size(); s > i; i++)
		{
			inserted(world.getEntity(ids[i]).getId());
		}
	}

	@Override
	public void inserted(int e)
	{
		FloorDrawableZone dz = new FloorDrawableZone(this.world, e);
		this.drawableZones.put(e, dz);
		this.getWorld().getSystem(Graphics2DSystem.class).addDrawableZone(dz);

	}

	public void addLayer(FloorDrawableLayer layer)
	{
		this.getWorld().getSystem(Graphics2DSystem.class).addLayer(layer);
	}

	public static abstract class FloorDrawableLayer
			extends DrawableLayer<FloorDrawableZone> {

		public FloorDrawableLayer(boolean enabled)
		{
			super(enabled);
		}

	}

	public static class FloorDrawableZone implements DrawableZone {

		private int entityId;
		protected World world;

		public FloorDrawableZone(World world, int entityId)
		{
			this.entityId = entityId;
			this.world = world;
		}

		@Override
		public float getMaxX()
		{
			return world.getEntity(entityId).getComponent(Floor.class)
					.getMaxX();
		}

		@Override
		public float getMaxY()
		{
			return world.getEntity(entityId).getComponent(Floor.class)
					.getMaxY();
		}

		@Override
		public float getMinX()
		{
			return world.getEntity(entityId).getComponent(Floor.class)
					.getMinX();
		}

		@Override
		public float getMinY()
		{
			return world.getEntity(entityId).getComponent(Floor.class)
					.getMinY();
		}

		@Override
		public String getName()
		{
			return world.getEntity(entityId).getComponent(NameComponent.class)
					.get();
		}

	}

	@Override
	protected void processSystem()
	{
		// TODO Auto-generated method stub

	}

	public void addLayer()
	{

	}

}

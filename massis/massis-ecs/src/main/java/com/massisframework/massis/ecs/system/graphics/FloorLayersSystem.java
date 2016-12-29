package com.massisframework.massis.ecs.system.graphics;

import static com.massisframework.massis.ecs.util.EntitiesCollections.iterate;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.Entity;
import com.artemis.World;
import com.massisframework.gui.DrawableLayer;
import com.massisframework.gui.DrawableZone;
import com.massisframework.massis.ecs.components.Floor;
import com.massisframework.massis.ecs.components.NameComponent;
import com.massisframework.massis.ecs.components.PolygonComponent;
import com.massisframework.massis.ecs.util.EntitiesCollections;

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
		EntitiesCollections.iterate(subscription).forEach(this::inserted);
		this.getWorld().getSystem(Graphics2DSystem.class).addLayer(new DL());
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

	private class DL extends FloorDrawableLayer {

		public DL()
		{
			super(true);
		}

		@Override
		public String getName()
		{
			return "Default Layer test";
		}

		@Override
		public void draw(FloorDrawableZone dz, Graphics2D g)
		{
			System.out.println("Drawing");
			for (Entity entity : iterate(dz.getFloor().getDoors(), world))
			{
				g.setColor(Color.green);
				g.fill(entity.getComponent(PolygonComponent.class).get());
			}
			for (Entity entity : iterate(dz.getFloor().getWalls(), world))
			{
				g.setColor(Color.BLUE);
				g.fill(entity.getComponent(PolygonComponent.class).get());
			}
			for (Entity entity : iterate(dz.getFloor().getRooms(), world))
			{
				g.setColor(Color.GRAY);
			}

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

		public Floor getFloor()
		{
			return this.world.getEntity(this.entityId)
					.getComponent(Floor.class);
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

	}

}

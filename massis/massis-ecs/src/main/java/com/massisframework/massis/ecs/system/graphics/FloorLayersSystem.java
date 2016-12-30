package com.massisframework.massis.ecs.system.graphics;

import static com.massisframework.massis.ecs.util.EntitiesCollections.iterate;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import org.jfree.fx.FXGraphics2D;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.Entity;
import com.artemis.World;
import com.massisframework.massis.ecs.components.Floor;
import com.massisframework.massis.ecs.components.NameComponent;
import com.massisframework.massis.ecs.components.PolygonComponent;
import com.massisframework.massis.ecs.util.EntitiesCollections;
import com.massisframework.massis.javafx.canvas2d.CanvasDrawable;
import com.massisframework.massis.javafx.canvas2d.CanvasLayer;

import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;

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
//		this.getWorld().getSystem(Graphics2DSystem.class).addLayer(new DL());
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
//		this.getWorld().getSystem(Graphics2DSystem.class).addLayer(layer);
	}

	public static abstract class FloorDrawableLayer
			extends CanvasLayer<FloorDrawableZone> {

		public FloorDrawableLayer(FloorDrawableZone drawable, String name)
		{
			super(drawable, name);
		}
	}

	private class DL extends FloorDrawableLayer {

		private Map<GraphicsContext,FXGraphics2D> graphics;
		
		public DL(FloorDrawableZone fdz)
		{
			super(fdz,"Default Layer test");
			this.graphics=new HashMap<>();
		}

		@Override
		protected void draw(FloorDrawableZone model, GraphicsContext gc)
		{
			FXGraphics2D gr = this.graphics.get(gc);
			if (gr==null){
				gr=new FXGraphics2D(gc);
				this.graphics.put(gc, gr);
			}
			this.draw(model,gr);
			
		}
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

	public static class FloorDrawableZone
			implements CanvasDrawable<Pair<Integer, World>> {

		Pair<Integer, World> model;
		private Integer entityId;
		private World world;

		public FloorDrawableZone(World world, int entityId)
		{
			this.model = new Pair<Integer, World>(entityId, world);
			this.entityId = this.model.getKey();
			this.world = this.model.getValue();
		}

		public Floor getFloor()
		{
			return this.world.getEntity(this.entityId)
					.getComponent(Floor.class);
		}

		@Override
		public double getMaxX()
		{
			return world.getEntity(entityId).getComponent(Floor.class)
					.getMaxX();
		}

		@Override
		public double getMaxY()
		{
			return world.getEntity(entityId).getComponent(Floor.class)
					.getMaxY();
		}

		@Override
		public double getMinX()
		{
			return world.getEntity(entityId).getComponent(Floor.class)
					.getMinX();
		}

		@Override
		public double getMinY()
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

		@Override
		public Pair<Integer, World> getModel()
		{
			return this.model;
		}

	}

	@Override
	protected void processSystem()
	{

	}

}

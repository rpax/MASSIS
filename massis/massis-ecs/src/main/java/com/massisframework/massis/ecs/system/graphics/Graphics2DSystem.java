package com.massisframework.massis.ecs.system.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.utils.IntBag;
import com.massisframework.gui.DrawableLayer;
import com.massisframework.gui.DrawableTabbedFrame;
import com.massisframework.gui.DrawableZone;
import com.massisframework.massis.ecs.components.PolygonComponent;
import com.massisframework.massis.ecs.system.graphics.FloorLayersSystem.FloorDrawableLayer;
import com.massisframework.massis.ecs.util.EntitiesCollections;

public class Graphics2DSystem extends BaseEntitySystem {

	private DrawableTabbedFrame frame;
	private IntBag entitiesToDraw;

	public Graphics2DSystem()
	{
		super(Aspect.all(PolygonComponent.class));
	}

	@Override
	protected void initialize()
	{
		super.initialize();
		this.entitiesToDraw = new IntBag();

	}

	@Override
	protected void processSystem()
	{
		IntBag actives = this.getEntityIds();
		entitiesToDraw.clear();
		int[] ids = actives.getData();
		for (int i = 0, s = actives.size(); s > i; i++)
		{
			entitiesToDraw.add(ids[i]);
		}
	}

	private class DL extends DrawableLayer<DrawableZone> {

		public DL()
		{
			super(true);
		}

		@Override
		public void draw(DrawableZone drawableZone, Graphics2D g)
		{
			g.setColor(Color.yellow);

			for (Integer entityId : EntitiesCollections.iterate(entitiesToDraw))
			{
				g.draw(world.getEntity(entityId)
						.getComponent(PolygonComponent.class).get());
			}
		}

		@Override
		public String getName()
		{
			return "Default Layer test";
		}

	}

	public void addDrawableZone(DrawableZone dz)
	{
		SwingUtilities.invokeLater(() -> {
			if (this.frame == null)
			{
				this.frame = new DrawableTabbedFrame(Arrays.asList(dz), "",
						new DL());
				// this.frame.pack();
				this.frame.setVisible(true);
				this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			} else
			{
				this.frame.addDrawableZone(dz);
			}
		});
	}

	public void addLayer(FloorDrawableLayer layer)
	{
		// TODO Auto-generated method stub
	}

}

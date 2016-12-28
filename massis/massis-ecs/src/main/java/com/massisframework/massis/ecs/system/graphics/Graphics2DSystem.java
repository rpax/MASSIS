package com.massisframework.massis.ecs.system.graphics;

import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.massisframework.gui.DrawableTabbedFrame;
import com.massisframework.gui.DrawableZone;
import com.massisframework.massis.ecs.components.BuildingLocation;
import com.massisframework.massis.ecs.components.PolygonComponent;
import com.massisframework.massis.ecs.system.graphics.FloorLayersSystem.FloorDrawableLayer;

public class Graphics2DSystem extends BaseEntitySystem {

	private DrawableTabbedFrame frame;
	// private Map<Integer, IntBag> entitiesToDraw;

	public Graphics2DSystem()
	{
		super(Aspect.all(BuildingLocation.class, PolygonComponent.class));
	}

	@Override
	protected void initialize()
	{
		super.initialize();
		// this.entitiesToDraw = new HashMap<>();

	}

	@Override
	protected void processSystem()
	{
		getFrame().refresh();
		// this.entitiesToDraw.values().forEach(IntBag::clear);
		// iterate(subscription, world).forEach(entity -> {
		// int fid = entity.getComponent(BuildingLocation.class).getFloorId();
		// IntBag entities = entitiesToDraw.get(fid);
		// if (entities == null)
		// {
		// entities = new IntBag();
		// entitiesToDraw.put(fid, entities);
		// }
		// entities.add(entity.getId());
		// });
	}

	public void addDrawableZone(DrawableZone dz)
	{
		SwingUtilities.invokeLater(() -> {
			this.getFrame().addDrawableZone(dz);
		});
	}

	private synchronized DrawableTabbedFrame getFrame()
	{
		if (this.frame == null)
		{
			this.frame = new DrawableTabbedFrame(Collections.emptyList(), "");
			// this.frame.pack();
			this.frame.setVisible(true);
			this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		return this.frame;
	}

	public void addLayer(FloorDrawableLayer layer)
	{
		SwingUtilities.invokeLater(() -> {
			this.getFrame().addDrawableLayer(layer);
		});
	}

}

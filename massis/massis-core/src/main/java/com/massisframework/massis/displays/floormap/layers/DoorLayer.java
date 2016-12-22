package com.massisframework.massis.displays.floormap.layers;

import static com.massisframework.massis.displays.floormap.layers.FloorMapLayersUtils.getShape;

import java.awt.Color;
import java.awt.Graphics2D;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.model.components.building.DoorComponent;
import com.massisframework.massis.model.components.building.ShapeComponent;
import com.massisframework.massis.sim.SimulationEntity;

/**
 * Draws doors and teleports
 *
 * @author rpax
 *
 */
public class DoorLayer extends DrawableLayer<DrawableFloor> {

	public DoorLayer(boolean enabled)
	{
		super(enabled);
	}

	@Override
	public void draw(DrawableFloor dfloor, Graphics2D g)
	{

		g.setColor(Color.green);
		for (SimulationEntity d : dfloor.getEntitiesFor(
				ShapeComponent.class,
				DoorComponent.class))
		{
			g.fill(getShape(d));
		}
		/*
		 * Teleport drawing. Depending on the type of teleport, one color or
		 * another is used.
		 */
		// for (SimulationEntity se : f.getTeleports())
		// {
		// Shape s = getShape(se);
		// TeleportComponent t = se.get(TeleportComponent.class);
		// g.setColor(Color.magenta);
		// g.draw(s);
		// if (t.getTeleportType() == TeleportType.START)
		// {
		// g.setColor(Color.GREEN.darker());
		// } else
		// {
		// g.setColor(Color.red);
		// }
		// g.fill(s);
		// }
	}

	@Override
	public String getName()
	{
		return "Doors";
	}
}

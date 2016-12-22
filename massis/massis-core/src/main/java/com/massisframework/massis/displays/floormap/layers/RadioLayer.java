package com.massisframework.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.model.components.Location;
import com.massisframework.massis.model.components.building.MovementCapabilities;
import com.massisframework.massis.model.components.building.ShapeComponent;
import com.massisframework.massis.sim.SimulationEntity;

/**
 * Displays the radio of each agent.
 *
 * @author rpax
 *
 */
public class RadioLayer extends DrawableLayer<DrawableFloor> {

	private static final Color RADIO_COLOR = Color.CYAN;

	public RadioLayer(boolean enabled)
	{
		super(enabled);
	}

	@Override
	public void draw(DrawableFloor dfloor, Graphics2D g)
	{

		g.setColor(RADIO_COLOR);
		Iterable<SimulationEntity> entities = dfloor.getEntitiesFor(
				ShapeComponent.class, MovementCapabilities.class,
				Location.class);
		for (SimulationEntity se : entities)
		{
			if (se.get(MovementCapabilities.class).canMove())
			{
				Location p = se.get(Location.class);
				FloorMapLayersUtils.drawCircle(g, p.getX(), p.getY(),
						se.get(ShapeComponent.class).getRadius());
			}

		}

	}

	@Override
	public String getName()
	{
		return "Body Radios";
	}
}

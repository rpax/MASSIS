package com.massisframework.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.building.Floor;

/**
 * Displays the radio of each agent.
 *
 * @author rpax
 *
 */
public class RadioLayer extends DrawableLayer<DrawableFloor> {

	private static final Color RADIO_COLOR = Color.CYAN;

	public RadioLayer(boolean enabled) {
		super(enabled);
	}

	@Override
	public void draw(DrawableFloor dfloor, Graphics2D g) {
		final Floor f = dfloor.getFloor();
		g.setColor(RADIO_COLOR);
		for (DefaultAgent p : f.getPeople()) {
			if (p.isDynamic()) {
				FloorMapLayersUtils.drawCircle(g, p.getXY(), p.getPolygon().getRadius());
			}

		}

	}

	@Override
	public String getName() {
		return "Body Radios";
	}
}

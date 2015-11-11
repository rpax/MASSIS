package com.massisframework.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.building.Floor;

/**
 * Displays the radio of each agent.
 *
 * @author rpax
 *
 */
public class RadioLayer extends FloorMapLayer {

	private static final Color RADIO_COLOR = Color.CYAN;

	public RadioLayer(boolean enabled) {
		super(enabled);
	}

	@Override
	protected void draw(Floor f, Graphics2D g) {
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

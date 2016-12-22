package com.massisframework.massis.displays.buildingmap;

import java.util.ArrayList;
import java.util.Collection;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.gui.DrawableTabbedFrame;
import com.massisframework.massis.displays.SimulationDisplay;
import com.massisframework.massis.displays.floormap.layers.DrawableFloor;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.sim.SimulationEntity;
import com.massisframework.massis.sim.engine.SimulationEngine;

public class BuildingMap extends DrawableTabbedFrame implements SimulationDisplay {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BuildingMap(SimulationEngine engine,DrawableLayer<DrawableFloor>[] layers) {
		super(getDrawableZones(engine),getWelcomeText(),layers);
	}

	@Override
	public void animate(SimulationEntity obj) {
		this.refresh();

	}

	@Override
	public boolean isDisplayEnabled() {
		return this.isVisible();
	}

	private static Collection<DrawableFloor> getDrawableZones(SimulationEngine engine) {
		final Collection<DrawableFloor> drawableZones = new ArrayList<>();
		//for (final Floor f : building.getFloors()) {
		System.out.println(engine.getEntitiesFor(Floor.class));
		for (SimulationEntity se : engine.getEntitiesFor(Floor.class))
		{
			drawableZones.add(new DrawableFloor(se.get(Floor.class),engine));
		}
			
		return drawableZones;
	}

	private static String getWelcomeText() {
		return ("<html>\n<head></head><body>\n<p><span style=\"font-size: x-large;\"><strong><span style=\"font-family: verdana, geneva;\"><span style=\"color: #3366ff;\"> Welcome to Simulation 2D display</span></span></strong></span></p>\n<p><span style=\"font-size: medium; font-family: verdana, geneva;\"><strong><span style=\"color: #3366ff;\"><br /></span></strong></span></p>\n<p style=\"color: #ff0000;\"><span style=\"font-family: verdana, geneva; font-size: medium;\"><span style=\"font-size: large;\"><strong><span style=\"color: #000000;\">Layers</span></strong></span><span style=\"text-decoration: underline; color: #666699;\"><strong><br /></strong></span></span></p>\n<p style=\"color: #ff0000;\"><span style=\"color: #000000; font-size: medium; font-family: verdana, geneva;\"><strong><br /></strong></span></p>\n<p><span style=\"font-family: verdana, geneva; font-size: small;\">The simulation 2D display system is organized in <strong>Layers</strong>.&nbsp;Each layer shows different elements from the simulation.Layers can be be enabled or disabled from the top left Layers menu.</span></p>\n<p><span style=\"font-family: verdana, geneva; font-size: medium;\"><br /></span></p>\n<p><span style=\"font-family: verdana, geneva; font-size: large;\"><strong>Zooming</strong></span></p>\n<p><span style=\"font-size: medium; font-family: verdana, geneva;\"><strong><br /></strong></span></p>\n<p><span style=\"font-size: medium; font-family: verdana, geneva;\"><span style=\"font-size: small;\">You can zoom moving the mouse wheel.</span></span></p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p><span style=\"font-family: verdana, geneva; font-size: large;\"><strong>Panning</strong></span></p>\n<p><span style=\"font-family: verdana, geneva; font-size: medium;\"><strong><br /></strong></span></p>\n<p><span style=\"font-family: verdana, geneva; font-size: small;\">The display allows moving the map, in a drag'drop fashion. Just click with the left mouse button and move it.</span></p>\n<p>&nbsp;</p>\n</body>\n</html>");

	}

}

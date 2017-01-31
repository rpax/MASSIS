package com.massisframework.massis.displays.floormap.layers;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.sim.ecs.SimulationComponent;

public class LayerComponent implements SimulationComponent {

	private DrawableLayer<?> layer;

	public DrawableLayer<?> getLayer()
	{
		return this.layer;
	}

	public void setLayer(DrawableLayer<?> layer)
	{
		this.layer = layer;
	}

}

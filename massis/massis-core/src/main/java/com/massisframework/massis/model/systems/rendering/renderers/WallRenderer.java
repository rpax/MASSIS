package com.massisframework.massis.model.systems.rendering.renderers;

import com.massisframework.massis.model.components.JFXRenderer;
import com.massisframework.massis.model.components.ShapeComponent;
import com.massisframework.massis.model.systems.floor.WallComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class WallRenderer implements JFXRenderer {

	// public static final WallRenderer renderer = new WallRenderer();

	@Override
	public void render(SimulationEntity e, GraphicsContext gc)
	{
		ShapeComponent sc = e.get(ShapeComponent.class);
		RenderUtils.fill(gc, sc, Color.BLACK);
		RenderUtils.stroke(gc, sc, Color.BLACK);

	}

	@Override
	public boolean matches(SimulationEntity e)
	{
		return e.has(WallComponent.class);
	}
}

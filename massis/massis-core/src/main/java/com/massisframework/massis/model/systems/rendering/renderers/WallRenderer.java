package com.massisframework.massis.model.systems.rendering.renderers;

import com.massisframework.massis.model.components.JFXRenderer;
import com.massisframework.massis.model.components.ShapeComponent;
import com.massisframework.massis.sim.ecs.OLDSimulationEntity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class WallRenderer implements JFXRenderer {

	public static final WallRenderer renderer = new WallRenderer();

	@Override
	public void render(OLDSimulationEntity<?> e, GraphicsContext gc)
	{
		ShapeComponent sc = e.get(ShapeComponent.class);
		RenderUtils.fill(gc, sc, Color.YELLOW);
		RenderUtils.stroke(gc, sc, Color.BLACK);

	}
}

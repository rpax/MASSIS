package com.massisframework.massis.model.systems.rendering.renderers;

import com.massisframework.massis.model.components.JFXRenderer;
import com.massisframework.massis.model.components.ShapeComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RoomRenderer implements JFXRenderer {

	public static final JFXRenderer renderer = new RoomRenderer();

	@Override
	public void render(SimulationEntity<?> e, GraphicsContext g2c)
	{
		ShapeComponent sc = e.get(ShapeComponent.class);
		RenderUtils.fill(g2c, sc, Color.GRAY);
	}
}

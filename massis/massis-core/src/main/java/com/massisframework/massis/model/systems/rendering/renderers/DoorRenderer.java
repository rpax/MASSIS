package com.massisframework.massis.model.systems.rendering.renderers;

import com.massisframework.massis.model.components.DoorComponent;
import com.massisframework.massis.model.components.JFXRenderer;
import com.massisframework.massis.model.components.ShapeComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DoorRenderer implements JFXRenderer {

	public static final JFXRenderer renderer = new DoorRenderer();

	@Override
	public void render(SimulationEntity e, GraphicsContext g2c)
	{
		ShapeComponent sc = e.get(ShapeComponent.class);
		DoorComponent dc = e.get(DoorComponent.class);
		if (dc.isOpen())
		{
			RenderUtils.fill(g2c, sc, Color.GREEN);
		} else
		{
			RenderUtils.fill(g2c, sc, Color.RED);
		}
	}

}

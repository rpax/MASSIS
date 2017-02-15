package com.massisframework.massis.model.systems.rendering.renderers;

import java.util.List;

import com.massisframework.massis.model.components.JFXRenderer;
import com.massisframework.massis.model.systems.PathComponent;
import com.massisframework.massis.model.systems.PathComponentImpl;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.util.geom.CoordinateHolder;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PathRenderer implements JFXRenderer {

	@Override
	public void render(SimulationEntity e, GraphicsContext gc)
	{
		PathComponent pc = e.get(PathComponent.class);
		List<CoordinateHolder> path = pc.getPath();
		for (int i = 0; i < path.size() - 1; i++)
		{
			CoordinateHolder a = path.get(i);
			CoordinateHolder b = path.get(i + 1);
			RenderUtils.stroke(gc, a.getX(), a.getY(), b.getX(), b.getY(),
					Color.YELLOW, 1);
		}
	}

	@Override
	public boolean matches(SimulationEntity e)
	{
		return e.has(PathComponent.class);
	}

}

package com.massisframework.massis.model.systems.rendering.renderers;

import com.massisframework.massis.model.components.JFXRenderer;
import com.massisframework.massis.model.components.StationaryObstacle;
import com.massisframework.massis.sim.ecs.SimulationEntity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import straightedge.geom.path.PathBlockingObstacle;

public class ObstacleRenderer implements JFXRenderer {

	@Override
	public void render(SimulationEntity e, GraphicsContext gc)
	{
		// Render lines
		for (PathBlockingObstacle obst : e.get(StationaryObstacle.class)
				.getObstacles())
		{
			RenderUtils.stroke(gc, obst.getOuterPolygon(), Color.YELLOW, 1);
		}

	}

	@Override
	public boolean matches(SimulationEntity e)
	{
		return e.has(StationaryObstacle.class);
	}

}

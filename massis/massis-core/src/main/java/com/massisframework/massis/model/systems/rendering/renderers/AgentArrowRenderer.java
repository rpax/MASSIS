package com.massisframework.massis.model.systems.rendering.renderers;

import com.massisframework.massis.model.components.JFXRenderer;
import com.massisframework.massis.model.components.ShapeComponent;
import com.massisframework.massis.model.systems.furniture.AgentComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import straightedge.geom.KPolygon;

public class AgentArrowRenderer implements JFXRenderer {

	private static ThreadLocal<KPolygon> triangle_render_TL = ThreadLocal
			.withInitial(() -> KPolygon.createRegularPolygon(3, 1));

	//public static JFXRenderer renderer = new AgentArrowRenderer();

	@Override
	public void render(SimulationEntity e, GraphicsContext gc)
	{
		RenderUtils.fill(gc, e.get(ShapeComponent.class), Color.WHITE);
		RenderUtils.stroke(gc, e.get(ShapeComponent.class), Color.BLACK);
	}

	@Override
	public boolean matches(SimulationEntity e)
	{
		return e.has(AgentComponent.class);
	}

}

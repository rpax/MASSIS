package com.massisframework.massis.model.systems.rendering.renderers;

import com.massisframework.massis.model.components.JFXRenderer;
import com.massisframework.massis.model.components.Orientation;
import com.massisframework.massis.model.components.Position2D;
import com.massisframework.massis.model.components.ShapeComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import straightedge.geom.KPolygon;

public class AgentArrowRenderer implements JFXRenderer {

	private static ThreadLocal<KPolygon> triangle_render_TL = ThreadLocal
			.withInitial(() -> KPolygon.createRegularPolygon(3, 1));

	public static JFXRenderer renderer = new AgentArrowRenderer();

	@Override
	public void render(SimulationEntity<?> e, GraphicsContext gc)
	{
		KPolygon triangle = triangle_render_TL.get();

		Position2D pos = e.get(Position2D.class);

		ShapeComponent sc = e.get(ShapeComponent.class);
		double radius = sc.getRadius();
		double scale =  radius;
		triangle.scale(scale,scale*0.6);
		triangle.translateTo(pos.getX(), pos.getY());
		triangle.rotate(e.get(Orientation.class).getAngle());

		RenderUtils.fill(gc, triangle, Color.WHITE);
		RenderUtils.stroke(gc, triangle, Color.BLUE);
		gc.setStroke(Color.CYAN);
		gc.strokeOval(pos.getX() - radius, pos.getY() - radius,
				radius * 2, radius * 2);
		
		triangle.scale(1/scale,1/(scale*0.6));
		triangle.translateTo(0,0);
		triangle.rotate(-e.get(Orientation.class).getAngle());
	}

}

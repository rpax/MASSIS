package com.massisframework.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.model.DrawableFloor;
import com.massisframework.massis.model.components.DynamicObstacle;
import com.massisframework.massis.model.components.Floor;
import com.massisframework.massis.model.components.Orientation;
import com.massisframework.massis.model.components.Position2D;
import com.massisframework.massis.model.components.ShapeComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;

import straightedge.geom.KPolygon;

public class PeopleLayer extends DrawableLayer<DrawableFloor> {

	public PeopleLayer(boolean enabled)
	{
		super(enabled);
	}

	private static Color DEFAULT_PERSON_FILL_COLOR = Color.WHITE;
	private static Color DEFAULT_PERSON_DRAW_COLOR = Color.BLUE;

	@Override
	public void draw(DrawableFloor dfloor, Graphics2D g)
	{
		final Floor f = dfloor.getFloor().getComponent(Floor.class);
		g.setColor(Color.red);

		for (SimulationEntity p : f.getEntitiesIn())
		{
			if (p.getComponent(DynamicObstacle.class) != null)
			{
				g.setColor(new Color(165, 42, 42));
				g.fill(p.getComponent(ShapeComponent.class).getShape());
			}
		}
		for (SimulationEntity p : f.getEntitiesIn())
		{
			if (p.getComponent(DynamicObstacle.class) != null)
			{
				KPolygon poly = KPolygon.createRegularPolygon(3,
						p.getComponent(ShapeComponent.class).getShape()
								.getRadius());
				poly.scale(1, 0.6);

				Position2D pos = p.getComponent(Position2D.class);
				poly.rotate(p.getComponent(Orientation.class).getAngle());
				poly.translateTo(pos.getX(), pos.getY());

				g.setColor(DEFAULT_PERSON_FILL_COLOR);

				g.fill(poly);
				g.setColor(DEFAULT_PERSON_DRAW_COLOR);
				g.draw(poly);
			}
		}

	}

	@Override
	public String getName()
	{
		return "People";
	}
}

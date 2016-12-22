package com.massisframework.massis.displays.floormap.layers;

import static com.massisframework.massis.displays.floormap.layers.FloorMapLayersUtils.getShape;

import java.awt.Color;
import java.awt.Graphics2D;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.model.components.Location;
import com.massisframework.massis.model.components.building.MovementCapabilities;
import com.massisframework.massis.model.components.building.ShapeComponent;
import com.massisframework.massis.sim.SimulationEntity;

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
		g.setColor(Color.red);
		Iterable<SimulationEntity> agents = dfloor.getEntitiesFor(
				ShapeComponent.class,
				MovementCapabilities.class,
				Location.class);
		for (SimulationEntity se : agents)
		{

			if (!se.get(MovementCapabilities.class).canMove())
			{
				g.setColor(new Color(165, 42, 42));
				g.fill(getShape(se));
			}
		}
		for (SimulationEntity se : agents)
		{

			if (!se.get(MovementCapabilities.class).canMove())
			{
				KPolygon poly = KPolygon.createRegularPolygon(3,
						se.get(ShapeComponent.class)
								.getRadius());
				poly.scale(1, 0.6);

				// FIXME orientation
				// poly.rotate(p.getAngle());

				Location p = se.get(Location.class);
				  
				poly.translateTo(p.getX(), p.getY());

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

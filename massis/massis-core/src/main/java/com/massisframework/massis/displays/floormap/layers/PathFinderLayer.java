package com.massisframework.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.model.components.building.ObstacleComponent;

import straightedge.geom.KPoint;
import straightedge.geom.path.PathBlockingObstacle;

/**
 * Shows the obstacles as they are in the corresponding path finder of each
 * floor.
 *
 * @author rpax
 *
 */
public class PathFinderLayer extends DrawableLayer<DrawableFloor> {

	public PathFinderLayer(boolean enabled)
	{
		super(enabled);
	}

	@Override
	public void draw(DrawableFloor f, Graphics2D g)
	{
		g.setColor(Color.yellow);
		int ovalRad = 10;
		Iterable<PathBlockingObstacle> obstacles = f
				.getEntitiesForStream(ObstacleComponent.class)
				.map(e -> e.get(ObstacleComponent.class))
				.map(ObstacleComponent::getObstacle)::iterator;
		
		for (PathBlockingObstacle obst : obstacles)
		{
			g.draw(obst.getOuterPolygon());
			for (KPoint p : obst.getOuterPolygon().getPoints())
			{
				g.fillOval((int) p.x - ovalRad / 2, (int) p.y - ovalRad / 2,
						ovalRad, ovalRad);
			}
		}

	}

	@Override
	public String getName()
	{
		return "PathFinder layer";
	}
}

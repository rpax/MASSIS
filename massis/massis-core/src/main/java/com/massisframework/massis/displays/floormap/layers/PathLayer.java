package com.massisframework.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import com.massisframework.gui.DrawableLayer;

/**
 * Draws the path of each agent. (if they have any)
 *
 * @author rpax
 *
 */
public class PathLayer extends DrawableLayer<DrawableFloor> {

	public PathLayer(boolean enabled) {
		super(enabled);
	}

	@Override
	public void draw(DrawableFloor dfloor, Graphics2D g) {
		g.setColor(Color.magenta);
//		// Agents...
//		dfloor.getEntitiesForStream(ShapeComponent.class,MovementCapabilities.class)
//		.filter(e->e.get(MovementCapabilities.class).canMove())::iterator;
//		
//		
//		for (LowLevelAgent p : f.getAgents())
//		{
//			if (p.hasPath())
//			{
//				List<WayPoint> path = new ArrayList<>(p.getPath().getPoints());
//				for (int i = 0; i < path.size() - 1; i++)
//				{
//					FloorMapLayersUtils.drawLine(g, path.get(i),
//							path.get(i + 1));
//					g.drawLine((int) path.get(i).getX(),
//							(int) path.get(i).getY(),
//							(int) path.get(i + 1).getX(),
//							(int) path.get(i + 1).getY());
//					g.fillOval((int) path.get(i + 1).getX(),
//							(int) path.get(i + 1).getY(), 10, 10);
//				}
//
//			}
//		}

	}

	@Override
	public String getName() {
		return "Paths";
	}
}

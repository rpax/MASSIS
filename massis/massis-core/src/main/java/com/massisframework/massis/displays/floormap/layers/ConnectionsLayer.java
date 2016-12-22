package com.massisframework.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.model.components.building.ObstacleComponent;
import com.massisframework.massis.sim.SimulationEntity;

import straightedge.geom.path.KNode;
import straightedge.geom.path.KNodeOfObstacle;

/**
 * Layer wich displays the connections of the pathfinder.
 *
 * @author rpax
 *
 */
public class ConnectionsLayer extends DrawableLayer<DrawableFloor> {
	// TODO remove engine from constructor-> demo para juan
	public ConnectionsLayer(boolean enabled)
	{
		super(enabled);
	}

	@Override
	public void draw(DrawableFloor f, Graphics2D g)
	{

		g.setColor(Color.DARK_GRAY);
		Iterable<SimulationEntity> entities = f
				.getEntitiesFor(ObstacleComponent.class);
		// for (PathBlockingObstacleImpl obst : f.getStationaryObstacles())
		for (SimulationEntity e : entities)
		{
			ObstacleComponent obst = e.get(ObstacleComponent.class);
			for (KNodeOfObstacle currentNode : obst.getObstacle().getNodes())
			{
				for (KNode n : currentNode.getConnectedNodes())
				{

					g.draw(new Line2D.Double(currentNode.getPoint().x,
							currentNode.getPoint().y, n.getPoint().getX(), n
									.getPoint().getY()));
				}
			}
		}

	}

	@Override
	public String getName()
	{
		return "Connections";
	}
}

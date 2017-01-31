package com.massisframework.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.massisframework.gui.AbstractDrawableLayer;
import com.massisframework.gui.EngineDrawableZone;
import com.massisframework.massis.model.components.StationaryObstacle;
import com.massisframework.massis.sim.FilterParams;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationEntity;

import straightedge.geom.path.KNode;
import straightedge.geom.path.KNodeOfObstacle;
import straightedge.geom.path.PathBlockingObstacle;

/**
 * Layer wich displays the connections of the pathfinder.
 *
 * @author rpax
 *
 */
public class ConnectionsLayer extends AbstractDrawableLayer {

	@Inject
	SimulationEngine engine;

	@FilterParams(all = {
			StationaryObstacle.class
	})
	private ComponentFilter stationaryFilter;

	
	
	
	private List<SimulationEntity> stationaryObstacles = new ArrayList<>();

	@Override
	public void draw(EngineDrawableZone drawableZone, Graphics2D g)
	{

		g.setColor(Color.DARK_GRAY);
		for (SimulationEntity se : engine
				.getEntitiesFor(stationaryFilter, stationaryObstacles))
		{
			PathBlockingObstacle obst = se
					.get(StationaryObstacle.class).getObstacle();
			for (KNodeOfObstacle currentNode : obst.getNodes())
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

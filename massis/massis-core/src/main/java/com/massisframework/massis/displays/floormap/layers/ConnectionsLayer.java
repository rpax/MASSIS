package com.massisframework.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.model.building.Floor;

import straightedge.geom.path.KNode;
import straightedge.geom.path.KNodeOfObstacle;
import straightedge.geom.path.PathBlockingObstacleImpl;

/**
 * Layer wich displays the connections of the pathfinder.
 *
 * @author rpax
 *
 */
public class ConnectionsLayer extends DrawableLayer<DrawableFloor> {

    public ConnectionsLayer(boolean enabled)
    {
        super(enabled);
    }

    @Override
	public void draw(DrawableFloor dfloor, Graphics2D g)
    {

    	final Floor f = dfloor.getFloor();
        g.setColor(Color.DARK_GRAY);
        for (PathBlockingObstacleImpl obst : f.getStationaryObstacles())
        {
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

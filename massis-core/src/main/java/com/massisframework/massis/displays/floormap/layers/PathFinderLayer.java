package com.massisframework.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import com.massisframework.massis.model.building.Floor;

import straightedge.geom.KPoint;
import straightedge.geom.path.PathBlockingObstacleImpl;

/**
 * Shows the obstacles as they are in the corresponding path finder of each
 * floor.
 *
 * @author rpax
 *
 */
public class PathFinderLayer extends FloorMapLayer {

    public PathFinderLayer(boolean enabled)
    {
        super(enabled);
    }

    @Override
    protected void draw(Floor f, Graphics2D g)
    {
        g.setColor(Color.yellow);
        int ovalRad = 10;
        for (PathBlockingObstacleImpl obst : f.getStationaryObstacles())
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

package com.massisframework.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.building.Floor;

import straightedge.geom.KPoint;

/**
 * Draws the path of each agent. (if they have any)
 *
 * @author rpax
 *
 */
public class PathLayer extends FloorMapLayer {

    public PathLayer(boolean enabled)
    {
        super(enabled);
    }

    @Override
    protected void draw(Floor f, Graphics2D g)
    {
        g.setColor(Color.magenta);

        for (DefaultAgent p : f.getPeople())
        {

            List<KPoint> path = new ArrayList<>(p.getPath());
            for (int i = 0; i < path.size() - 1; i++)
            {

                g.drawLine((int) path.get(i).x, (int) path.get(i).y,
                        (int) path.get(i + 1).x, (int) path.get(i + 1).y);
                g.fillOval((int) path.get(i + 1).x, (int) path.get(i + 1).y,
                        10, 10);
            }

        }

    }

    @Override
    public String getName()
    {
        return "Paths";
    }
}

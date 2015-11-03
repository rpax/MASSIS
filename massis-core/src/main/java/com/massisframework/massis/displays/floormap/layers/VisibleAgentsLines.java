package com.massisframework.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.building.Floor;

/**
 * Shows the agents visible from another agent, in a network fashion
 *
 * @author rpax
 *
 */
public class VisibleAgentsLines extends FloorMapLayer {

    public VisibleAgentsLines(boolean enabled)
    {
        super(enabled);

    }

    @Override
    protected void draw(Floor f, Graphics2D g)
    {
        g.setColor(Color.WHITE);
        for (DefaultAgent v : f.getPeople())
        {
            if (!v.isDynamic())
            {
                continue;
            }
            for (DefaultAgent v2 : v.getAgentsInVisionRadio())
            {
                g.drawLine((int) v.getX(), (int) v.getY(), (int) v2.getX(),
                        (int) v2.getY());
            }
        }

    }

    @Override
    public String getName()
    {
        return "Visible agents lines";
    }
}

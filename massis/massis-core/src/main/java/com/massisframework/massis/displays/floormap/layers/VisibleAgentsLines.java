package com.massisframework.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.building.Floor;

/**
 * Shows the agents visible from another agent, in a network fashion
 *
 * @author rpax
 *
 */
public class VisibleAgentsLines extends DrawableLayer<DrawableFloor> {

    public VisibleAgentsLines(boolean enabled)
    {
        super(enabled);

    }

    @Override
    public void draw(DrawableFloor dfloor, Graphics2D g)
    {
    	final Floor f = dfloor.getFloor();
        g.setColor(Color.WHITE);
        for (LowLevelAgent v : f.getAgents())
        {
            if (!v.isDynamic())
            {
                continue;
            }
            for (LowLevelAgent v2 : v.getAgentsInVisionRadio())
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

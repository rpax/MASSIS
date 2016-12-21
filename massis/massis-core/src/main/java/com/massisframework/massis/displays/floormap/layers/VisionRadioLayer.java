package com.massisframework.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.building.IFloor;

/**
 * Draws the vision area of each agent
 *
 * @author rpax
 *
 */
public class VisionRadioLayer extends DrawableLayer<DrawableFloor> {

    public VisionRadioLayer(boolean enabled)
    {
        super(enabled);

    }

    @Override
    public void draw(DrawableFloor dfloor, Graphics2D g)
    {
    	final IFloor f = dfloor.getFloor();
        for (DefaultAgent p : f.getAgents())
        {
            if (!p.isDynamic())
            {
                continue;
            }

            g.setColor(Color.red);
            g.draw(p.getVisionRadioShape());

        }
    }

    @Override
    public String getName()
    {
        return "Vision radio view";
    }
}

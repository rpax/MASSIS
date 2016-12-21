package com.massisframework.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.building.Floor;

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
    	final Floor f = dfloor.getFloor();
        for (LowLevelAgent p : f.getAgents())
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

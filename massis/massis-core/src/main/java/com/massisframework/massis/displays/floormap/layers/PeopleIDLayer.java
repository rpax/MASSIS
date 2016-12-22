package com.massisframework.massis.displays.floormap.layers;

import java.awt.Font;
import java.awt.Graphics2D;

import com.massisframework.gui.DrawableLayer;

/**
 * Shows the UID number of each agent in the floor
 *
 * @author rpax
 *
 */
public class PeopleIDLayer extends DrawableLayer<DrawableFloor> {

    private static final Font ID_FONT = new Font("Georgia", Font.BOLD, 30);

    public PeopleIDLayer(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public void draw(DrawableFloor dfloor, Graphics2D g)
    {
//    	final Floor f = dfloor.getFloor();
//        g.setColor(Color.orange);
//        Font originalF = g.getFont();
//
//        g.setFont(ID_FONT);
//        for (LowLevelAgent p : f.getAgents())
//        {
//            g.drawChars(String.valueOf(p.getID()).toCharArray(), 0, String
//                    .valueOf(p.getID()).toCharArray().length,
//                    (int) p.getX(), (int) p.getY());
//        }
//        g.setFont(originalF);
    }

    @Override
    public String getName()
    {
        return "People ID layer";
    }
}

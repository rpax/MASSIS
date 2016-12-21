package com.massisframework.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.model.building.IFloor;
import com.massisframework.massis.model.building.SimDoor;
import com.massisframework.massis.model.building.Teleport;

/**
 * Draws doors and teleports
 *
 * @author rpax
 *
 */
public class DoorLayer extends DrawableLayer<DrawableFloor> {

    public DoorLayer(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public void draw(DrawableFloor dfloor, Graphics2D g)
    {
		final IFloor f = dfloor.getFloor();
        g.setColor(Color.green);
        for (SimDoor d : f.getDoors())
        {
            g.fill(d.getPolygon());
        }
        /*
         * Teleport drawing. Depending on the type of teleport, one color or
         * another is used.
         */
        for (Teleport d : f.getTeleports())
        {
            g.setColor(Color.magenta);
            g.draw(d.getPolygon());
            if (d.getType() == Teleport.START)
            {
                g.setColor(Color.GREEN.darker());
            } else
            {
                g.setColor(Color.red);
            }
            g.fill(d.getPolygon());
        }
    }

    @Override
    public String getName()
    {
        return "Doors";
    }
}

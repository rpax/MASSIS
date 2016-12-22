package com.massisframework.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.model.components.Location;
import com.massisframework.massis.model.components.NameComponent;
import com.massisframework.massis.model.components.RoomComponent;
import com.massisframework.massis.sim.SimulationEntity;

public class RoomsLabelLayer extends DrawableLayer<DrawableFloor> {

    private static final Font NAME_FONT = new Font("Georgia", Font.BOLD, 30);

    public RoomsLabelLayer(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public void draw(DrawableFloor dfloor, Graphics2D g)
    {
        g.setColor(Color.orange);
        Font originalF = g.getFont();
        g.setFont(NAME_FONT);
        for (SimulationEntity se: dfloor.getEntitiesFor(Location.class,NameComponent.class,RoomComponent.class))
        {
        	Location loc = se.get(Location.class);
        	NameComponent name=se.get(NameComponent.class);
            char[] chars = name.toString().toCharArray();
            g.setColor(Color.black);
            g.drawChars(chars, 0, chars.length, (int)loc.getX(),
                    (int)loc.getY());
        }
        g.setFont(originalF);
    }

    @Override
    public String getName()
    {
        return "Room Labels";
    }
}

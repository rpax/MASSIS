package com.massisframework.massis.displays.floormap.layers;

import static com.massisframework.massis.displays.floormap.layers.FloorMapLayersUtils.getShape;

import java.awt.Color;
import java.awt.Graphics2D;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.sim.SimulationEntity;/**
 * Draws each room of the floor
 *
 * @author rpax
 *
 */
public class RoomsLayer extends DrawableLayer<DrawableFloor> {

    public RoomsLayer(boolean enabled)
    {
        super(enabled);

    }

    @Override
    public void draw(DrawableFloor dfloor, Graphics2D g)
    {
    	final Floor f = dfloor.getFloor();
        for (SimulationEntity r : f.getRooms())
        {
            g.setColor(Color.gray);
            g.fill(getShape(r));
        }

    }

    @Override
    public String getName()
    {
        return "Rooms";
    }
}

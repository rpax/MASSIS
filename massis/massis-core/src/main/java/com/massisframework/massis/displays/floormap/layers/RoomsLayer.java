package com.massisframework.massis.displays.floormap.layers;

import static com.massisframework.massis.displays.floormap.layers.FloorMapLayersUtils.getShape;

import java.awt.Color;
import java.awt.Graphics2D;

import com.itextpdf.awt.geom.Shape;
import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.model.components.RoomComponent;
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
        for (SimulationEntity r : dfloor.getEntitiesFor(Shape.class,RoomComponent.class))
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

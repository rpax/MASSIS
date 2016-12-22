package com.massisframework.massis.displays.floormap.layers;

import static com.massisframework.massis.displays.floormap.layers.FloorMapLayersUtils.getShape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import com.massisframework.gui.DrawableLayer;
import com.massisframework.massis.model.components.building.ShapeComponent;
import com.massisframework.massis.model.components.building.WallComponent;
import com.massisframework.massis.sim.SimulationEntity;

/**
 * Draws the walls of a floor
 *
 * @author rpax
 *
 */
public class WallLayer extends DrawableLayer<DrawableFloor> {

    private static final Color WALL_COLOR = new Color(121, 197, 109);

    public WallLayer(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public void draw(DrawableFloor dfloor, Graphics2D g)
    {
        g.setColor(WALL_COLOR);
        Iterable<SimulationEntity> walls = dfloor.getEntitiesFor(WallComponent.class,ShapeComponent.class);
        for (SimulationEntity e : walls)
        {
        	Shape s=getShape(e);
            g.draw(s);
            g.fill(s);
        }

    }

    @Override
    public String getName()
    {
        return "Walls";
    }
}

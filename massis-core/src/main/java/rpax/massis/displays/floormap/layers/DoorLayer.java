package rpax.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import rpax.massis.model.building.Floor;
import rpax.massis.model.building.SimDoor;
import rpax.massis.model.building.Teleport;

/**
 * Draws doors and teleports
 *
 * @author rpax
 *
 */
public class DoorLayer extends FloorMapLayer {

    public DoorLayer(boolean enabled)
    {
        super(enabled);
    }

    @Override
    protected void draw(Floor f, Graphics2D g)
    {
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

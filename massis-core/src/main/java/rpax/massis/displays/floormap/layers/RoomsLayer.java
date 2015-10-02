package rpax.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import rpax.massis.model.building.Floor;
import rpax.massis.model.building.SimRoom;

/**
 * Draws each room of the floor
 *
 * @author rpax
 *
 */
public class RoomsLayer extends FloorMapLayer {

    public RoomsLayer(boolean enabled)
    {
        super(enabled);

    }

    @Override
    protected void draw(Floor f, Graphics2D g)
    {
        for (SimRoom r : f.getRooms())
        {
            g.setColor(Color.gray);
            g.fill(r.getPolygon());
        }

    }

    @Override
    public String getName()
    {
        return "Rooms";
    }
}

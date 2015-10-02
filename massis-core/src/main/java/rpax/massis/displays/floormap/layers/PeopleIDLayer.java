package rpax.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import rpax.massis.model.agents.DefaultAgent;
import rpax.massis.model.building.Floor;

/**
 * Shows the UID number of each agent in the floor
 *
 * @author rpax
 *
 */
public class PeopleIDLayer extends FloorMapLayer {

    private static final Font ID_FONT = new Font("Georgia", Font.BOLD, 30);

    public PeopleIDLayer(boolean enabled)
    {
        super(enabled);
    }

    @Override
    protected void draw(Floor f, Graphics2D g)
    {
        g.setColor(Color.orange);
        Font originalF = g.getFont();

        g.setFont(ID_FONT);
        for (DefaultAgent p : f.getPeople())
        {
            g.drawChars(String.valueOf(p.getID()).toCharArray(), 0, String
                    .valueOf(p.getID()).toCharArray().length,
                    (int) p.getXY().x, (int) p.getXY().y);
        }
        g.setFont(originalF);
    }

    @Override
    public String getName()
    {
        return "People ID layer";
    }
}

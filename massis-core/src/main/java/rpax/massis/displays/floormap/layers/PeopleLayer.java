package rpax.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import rpax.massis.model.agents.DefaultAgent;
import rpax.massis.model.building.Floor;
import straightedge.geom.KPolygon;

public class PeopleLayer extends FloorMapLayer {

    public PeopleLayer(boolean enabled)
    {
        super(enabled);
    }
    private static Color DEFAULT_PERSON_FILL_COLOR = Color.WHITE;
    private static Color DEFAULT_PERSON_DRAW_COLOR = Color.BLUE;

    @Override
    protected void draw(Floor f, Graphics2D g)
    {
        g.setColor(Color.red);

        for (DefaultAgent p : f.getPeople())
        {

            if (!p.isDynamic())
            {
                g.setColor(new Color(165, 42, 42));
                g.fill(p.getPolygon());
            } else
            {

                KPolygon poly = KPolygon.createRegularPolygon(3, p.getPolygon()
                        .getRadius());
                poly.scale(1, 0.6);

                poly.rotate(p.getAngle());
                poly.translateTo(p.getX(), p.getY());

                g.setColor(DEFAULT_PERSON_FILL_COLOR);

                g.fill(poly);
                g.setColor(DEFAULT_PERSON_DRAW_COLOR);
                g.draw(poly);

            }

        }

    }

    @Override
    public String getName()
    {
        return "People";
    }
}

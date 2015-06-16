package rpax.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import rpax.massis.model.building.Floor;
import rpax.massis.model.building.SimWall;

/**
 * Draws the walls of a floor
 * 
 * @author rpax
 * 
 */
public class WallLayer extends FloorMapLayer {

	private static final Color WALL_COLOR = new Color(121, 197, 109);

	public WallLayer(boolean enabled) {
		super(enabled);
	}

	@Override
	protected void draw(Floor f, Graphics2D g) {
		g.setColor(WALL_COLOR);
		for (SimWall wall : f.getWalls())
		{
			g.draw(wall.getPolygon());
			g.fill(wall.getPolygon());
		}

	}

	@Override
	public String getName() {
		return "Walls";
	}

}

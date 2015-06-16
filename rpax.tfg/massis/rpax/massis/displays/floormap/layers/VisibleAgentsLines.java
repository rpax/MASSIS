package rpax.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;

import rpax.massis.model.agents.Agent;
import rpax.massis.model.agents.FurnitureAgent;
import rpax.massis.model.building.Floor;

/**
 * Shows the agents visible from another agent, in a network fashion
 * 
 * @author rpax
 * 
 */
public class VisibleAgentsLines extends FloorMapLayer {

	public VisibleAgentsLines(boolean enabled) {
		super(enabled);

	}

	@Override
	protected void draw(Floor f, Graphics2D g) {
		g.setColor(Color.WHITE);
		for (Agent v : f.getPeople())
		{
			if (v instanceof FurnitureAgent)
				continue;
			for (Agent v2 : v.getAgentsInVisionRadio())
			{
				g.drawLine((int) v.getX(), (int) v.getY(), (int) v2.getX(),
						(int) v2.getY());
			}
		}

	}

	@Override
	public String getName() {
		return "Visible agents lines";
	}

}

package rpax.massis.displays.floormap.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import rpax.massis.model.building.Floor;
import straightedge.geom.path.KNode;
import straightedge.geom.path.KNodeOfObstacle;
import straightedge.geom.path.PathBlockingObstacleImpl;

/**
 * Layer wich displays the connections of the pathfinder.
 * 
 * @author rpax
 * 
 */
public class ConnectionsLayer extends FloorMapLayer {

	public ConnectionsLayer(boolean enabled) {
		super(enabled);
	}

	@Override
	protected void draw(Floor f, Graphics2D g) {

		g.setColor(Color.DARK_GRAY);
		for (PathBlockingObstacleImpl obst : f.getStationaryObstacles())
		{
			for (KNodeOfObstacle currentNode : obst.getNodes())
			{
				for (KNode n : currentNode.getConnectedNodes())
				{

					g.draw(new Line2D.Double(currentNode.getPoint().x,
							currentNode.getPoint().y, n.getPoint().getX(), n
									.getPoint().getY()));
				}
			}
		}

	}

	@Override
	public String getName() {
		return "Connections";
	}

}

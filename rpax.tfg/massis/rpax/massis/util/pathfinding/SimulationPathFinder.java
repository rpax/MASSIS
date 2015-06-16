package rpax.massis.util.pathfinding;

import java.awt.Graphics2D;
import java.util.ArrayList;

import rpax.massis.model.agents.Agent;
import rpax.massis.model.building.Floor;
import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;
import straightedge.geom.path.PathBlockingObstacleImpl;
/**
 * PathFinder interface of the simulation
 * @author rpax
 *
 */
public interface SimulationPathFinder {

	public abstract ArrayList<KPoint> findPath(Agent v, KPoint to);

	
	public abstract KPoint getNearestPointOutsideOfObstacles(KPoint p);
	public abstract Iterable<PathBlockingObstacleImpl> getStationaryObstacles();

	public abstract Iterable<KPolygon> getWalkableAreas();

	public abstract void initialize();

	public abstract Floor getFloor();
	public abstract void renderConnections(Graphics2D g);
}
package straightedge.geom.path;

import straightedge.geom.*;
import straightedge.geom.util.*;
import straightedge.geom.vision.Occluder;

import java.util.*;

/**
 * Finds a path through PathBlockingObstacles. based on Keith's
 * {@link PathFinder} class
 */

public class MASSISPathFinder {
	public KNode startNode;
	public KNode endNode;
	public BinaryHeap<KNode> openList;

	// Tracker is used in conjunction with the KNodes to detect if the Nodes are
	// in the open or closed state.
	Tracker tracker = new Tracker();

	// for debugging only:
	public boolean debug = false;
	public KPoint startPointDebug;
	public KPoint endPointDebug;
	public ArrayList<KNode> startNodeTempReachableNodesDebug = new ArrayList<KNode>();
	public ArrayList<KNode> endNodeTempReachableNodesDebug = new ArrayList<KNode>();

	public MASSISPathFinder() {
		openList = new BinaryHeap<KNode>();
		startNode = new KNode();
		endNode = new KNode();
	}

	public PathData calc(KPoint start, KPoint end,
			double maxTempNodeConnectionDist, MNodeConnector<?> nodeConnector,
			List<? extends PathBlockingObstacle> obstacles,Occluder restrictionPolygon) {
		return calc(start, end, maxTempNodeConnectionDist, Double.MAX_VALUE,
				nodeConnector, obstacles,restrictionPolygon);
	}

	/**
	 * @param start
	 * @param end
	 * @param maxTempNodeConnectionDist
	 *            Maximum connection distance from start to obstacles and end to
	 *            obstacles. The smaller the distance, the faster the algorithm.
	 * @param maxSearchDistStartToEnd
	 *            Maximum distance from start to end. Any paths with a longer
	 *            distance won't be returned. The smaller the value the faster
	 *            the algorithm.
	 * @param nodeConnector
	 * @param obstacles
	 * @param pointList
	 *            Path points from start to end. If there was no path found, or
	 *            no path found with a distance less than
	 *            maxSearchDistStartToEnd, an empty (zero-size) list will be
	 *            returned.
	 * @return
	 */
	public PathData calc(KPoint start, KPoint end,
			double maxTempNodeConnectionDist, double maxSearchDistStartToEnd,
			MNodeConnector<?> nodeConnector, List<? extends PathBlockingObstacle> obstacles,Occluder restrictionPolygon) {
		assert tempReachableNodesExist(obstacles) == false;
		double startToEndDist = start.distance(end);
		if (startToEndDist > maxSearchDistStartToEnd)
		{
			// no point doing anything since startToEndDist is greater than
			// maxSearchDistStartToEnd.
			PathData pathData = new PathData(PathData.Result.ERROR1);
			return pathData;
		}
		// KPoint midPoint = start.midPoint(end);
		// ArrayList obstacles = obstaclesTileArray.getAllWithin(midPoint,
		// startToEndDist/2f);
		// assert tempReachableNodesExist(obstacles) == false;

		startNode.clearForReuse();
		startNode.setPoint(start);
		// Set startNode gCost to zero
		startNode.calcGCost();
		KNode currentNode = startNode;
		endNode.clearForReuse();
		endNode.setPoint(end);

		// Check for straight line path between start and end.
		// Note that this assumes start and end are not both contained in the
		// same polygon.
		boolean intersection = false;
		ObstacleLoop: for (int i = 0; i < obstacles.size(); i++)
		{
			KPolygon innerPolygon = ((PathBlockingObstacle) obstacles.get(i))
					.getInnerPolygon();
			// Test if polygon intersects the line from start to end
			if (innerPolygon.intersectionPossible(start, end)
					&& innerPolygon.intersectsLine(start, end))
			{
				intersection = true;
				break ObstacleLoop;
			}
		}
		if (intersection == false)
		{
			// No intersections, so the straight-line path is fine!
			endNode.setParent(currentNode);
			PathData pathData = this.makePathData();
			clearTempReachableNodes();
			tracker.incrementCounter();
			return pathData;
		}
		{
			// Connect the startNode to its reachable nodes and vice versa
			//EL PRIMERO SI
			ArrayList<KNode> reachableNodes = nodeConnector
					.makeReachableNodesFor(startNode,
							maxTempNodeConnectionDist,restrictionPolygon);
			if (reachableNodes.size() == 0)
			{
				// path from start node is not possible since there are no
				// connections to it.
				PathData pathData = new PathData(PathData.Result.ERROR2);
				clearTempReachableNodes();
				tracker.incrementCounter();
				return pathData;
			}
			startNode.getTempConnectedNodes().addAll(reachableNodes);
			for (int i = 0; i < reachableNodes.size(); i++)
			{
				KNode node = reachableNodes.get(i);
				node.getTempConnectedNodes().add(startNode);
			}
//EL SEGUNDO NO
			// Connect the endNode to its reachable nodes and vice versa
			reachableNodes = nodeConnector.makeReachableNodesFor(endNode,
					maxTempNodeConnectionDist,null);
			if (reachableNodes.size() == 0)
			{
				// path to end node is not possible since there are no
				// connections to it.
				PathData pathData = new PathData(PathData.Result.ERROR3);
				clearTempReachableNodes();
				tracker.incrementCounter();
				return pathData;
			}
			endNode.getTempConnectedNodes().addAll(reachableNodes);
			for (int i = 0; i < reachableNodes.size(); i++)
			{
				KNode node = reachableNodes.get(i);
				node.getTempConnectedNodes().add(endNode);
			}
		}

		// Here we start the A* algorithm!
		openList.makeEmpty();
		while (true)
		{
			// put the current node in the closedSet and take it out of the
			// openList.
			currentNode.setPathFinderStatus(KNode.CLOSED, tracker);
			if (openList.isEmpty() == false)
			{
				openList.deleteMin();
			}
			// add reachable nodes to the openList if they're not already there.
			ArrayList<KNode> reachableNodes = currentNode.getConnectedNodes();
			for (int i = 0; i < reachableNodes.size(); i++)
			{
				KNode reachableNode = reachableNodes.get(i);
				if (reachableNode.getPathFinderStatus(tracker) == KNode.UNPROCESSED)
				{
					reachableNode.setParent(currentNode);
					reachableNode.calcHCost(endNode);
					reachableNode.calcGCost();
					reachableNode.calcFCost();
					if (reachableNode.getFCost() <= maxSearchDistStartToEnd)
					{
						openList.add(reachableNode);
						reachableNode.setPathFinderStatus(KNode.OPEN, tracker);
					}
				}
				else if (reachableNode.getPathFinderStatus(tracker) == KNode.OPEN)
				{
					assert reachableNode.getGCost() != KNode.G_COST_NOT_CALCULATED_FLAG;
					double currentGCost = reachableNode.getGCost();
					double newGCost = currentNode.getGCost()
							+ currentNode.getPoint().distance(
									reachableNode.getPoint());
					if (newGCost < currentGCost)
					{
						reachableNode.setParent(currentNode);
						reachableNode.setGCost(newGCost); // reachableNode.calcGCost();
						reachableNode.calcFCost();
						// Since the g-cost of the node has changed,
						// must re-sort the list to reflect this.
						int index = openList.indexOf(reachableNode);
						openList.percolateUp(index);
					}
				}
			}
			ArrayList<KNode> tempReachableNodes = currentNode
					.getTempConnectedNodes();
			for (int i = 0; i < tempReachableNodes.size(); i++)
			{
				KNode reachableNode = tempReachableNodes.get(i);
				if (reachableNode.getPathFinderStatus(tracker) == KNode.UNPROCESSED)
				{
					reachableNode.setParent(currentNode);
					reachableNode.calcHCost(endNode);
					reachableNode.calcGCost();
					reachableNode.calcFCost();
					if (reachableNode.getFCost() <= maxSearchDistStartToEnd)
					{
						openList.add(reachableNode);
						reachableNode.setPathFinderStatus(KNode.OPEN, tracker);
					}
				}
				else if (reachableNode.getPathFinderStatus(tracker) == KNode.OPEN)
				{
					assert reachableNode.getGCost() != KNode.G_COST_NOT_CALCULATED_FLAG;
					double currentGCost = reachableNode.getGCost();
					double newGCost = currentNode.getGCost()
							+ currentNode.getPoint().distance(
									reachableNode.getPoint());
					if (newGCost < currentGCost)
					{
						reachableNode.setParent(currentNode);
						reachableNode.setGCost(newGCost); // reachableNode.calcGCost();
						reachableNode.calcFCost();
						// Since the g-cost of the node has changed,
						// must re-sort the list to reflect this.
						int index = openList.indexOf(reachableNode);
						openList.percolateUp(index);
					}
				}
			}
			if (openList.size() == 0)
			{
				// System.out.println(this.getClass().getSimpleName()+": openList.size() == 0, returning");
				PathData pathData = new PathData(PathData.Result.ERROR4);
				clearTempReachableNodes();
				tracker.incrementCounter();
				return pathData;
			}

			currentNode = openList.peekMin();
			if (currentNode == endNode)
			{
				// System.out.println(this.getClass().getSimpleName()+": currentNode == endNode, returning");
				break;
			}
		}
		PathData pathData = makePathData();
		clearTempReachableNodes();
		tracker.incrementCounter();
		return pathData;
	}

	

	protected void clearTempReachableNodes() {
		if (debug)
		{
			startPointDebug = startNode.getPoint().copy();
			endPointDebug = endNode.getPoint().copy();
			startNodeTempReachableNodesDebug.clear();
			endNodeTempReachableNodesDebug.clear();
			startNodeTempReachableNodesDebug.addAll(startNode
					.getTempConnectedNodes());
			endNodeTempReachableNodesDebug.addAll(endNode
					.getTempConnectedNodes());
		}

		// Erase all nodes' tempConnectedNodes
		if (startNode != null)
		{
			startNode.clearTempConnectedNodes();
		}
		if (endNode != null)
		{
			endNode.clearTempConnectedNodes();
		}
	}

	ArrayList<KNode> nodes = new ArrayList<KNode>();
	ArrayList<KPoint> points = new ArrayList<KPoint>();

	protected PathData makePathData() {
		KNode currentNode = getEndNode();
		while (true)
		{
			nodes.add(currentNode);
			points.add(currentNode.getPoint());
			KNode parentNode = currentNode.getParent();
			if (parentNode == null)
			{
				break;
			}
			currentNode = parentNode;
		}
		Collections.reverse(nodes);
		Collections.reverse(points);
		PathData pathData = new PathData(points, nodes);
		nodes.clear();
		points.clear();
		return pathData;
	}

	public boolean pathExists() {
		if (getEndNode() != null && getEndNode().getParent() != null)
		{
			return true;
		}
		return false;
	}

	public KNode getEndNode() {
		return endNode;
	}

	public KNode getStartNode() {
		return startNode;
	}

	// used only for assertion checks
	protected boolean tempReachableNodesExist(List<? extends PathBlockingObstacle> obstacles) {
		for (int i = 0; i < obstacles.size(); i++)
		{
			PathBlockingObstacle obst =  obstacles.get(i);
			for (int j = 0; j < obst.getNodes().size(); j++)
			{
				KNodeOfObstacle node = obst.getNodes().get(j);
				if (node.getTempConnectedNodes().size() > 0)
				{
					return true;
				}
			}
		}
		return false;
	}
}

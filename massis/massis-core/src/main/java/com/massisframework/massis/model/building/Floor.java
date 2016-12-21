package com.massisframework.massis.model.building;

import java.util.ArrayList;
import java.util.List;

import com.eteks.sweethome3d.model.Level;
import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.pathfinding.straightedge.FindPathResult;
import com.massisframework.massis.util.Indexable;
import com.massisframework.massis.util.geom.ContainmentPolygon;
import com.massisframework.massis.util.io.Restorable;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;
import straightedge.geom.path.PathBlockingObstacleImpl;

public interface Floor extends Indexable{

	void initializePathFinder();

	ArrayList<ContainmentPolygon> getContainmentPolygons();

	/**
	 *
	 * @return a random Room in this floor
	 */
	SimRoom getRandomRoom();

	List<SimDoor> getDoors();

	int getMinX();

	int getMaxX();

	int getMinY();

	int getMaxY();
	
	public int getXlength();

	public int getYlength();

	List<SimWall> getWalls();

	List<SimRoom> getRooms();

	Iterable<LowLevelAgent> getAgents();

	int hashCode();

	boolean equals(Object obj);

	Iterable<PathBlockingObstacleImpl> getStationaryObstacles();

	Iterable<KPolygon> getWalkableAreas();

	String getName();

	Level getLevel();

	/**
	 * Removes an agent from this floor
	 *
	 * @param simObj
	 *            the agent to be removed
	 */
	void remove(Restorable simObj);

	/**
	 * Adds an agent to this floor
	 *
	 * @param simObj
	 */
	void addPerson(Restorable simObj);

	/**
	 * Finds a path in this floor. If the
	 *
	 * @param fromLoc
	 *            the starting location
	 * @param to
	 *            the desired location
	 * @return the path.
	 */
	void findPath(Location fromLoc, Location to,
			FindPathResult callback);

	/**
	 * Returns the available teleports in this floor that can be used to reach
	 * other floor
	 *
	 * @param other
	 *            the target floor
	 * @return a list of teleports that can be used to reach the other floor
	 */
	List<Teleport> getTeleportsConnectingFloor(Floor other);

	List<Teleport> getTeleports();

	List<RoomConnector> getRoomConnectors();

	int getID();

	KPoint getNearestPointOutsideOfObstacles(double x, double y);

	KPoint getNearestPointOutsideOfObstacles(KPoint p);

	/**
	 *
	 * @return the rectangles of the leaves of the QuadTree.
	 */
	Iterable<KPolygon> getQTRectangles();

	/**
	 *
	 * @param xmin
	 * @param ymin
	 * @param xmax
	 * @param ymax
	 * @return The agents inside the rectangle defined by xmin,ymin,xmax,ymax
	 */
	Iterable<LowLevelAgent> getAgentsInRange(int xmin, int ymin, int xmax,
			int ymax);

}
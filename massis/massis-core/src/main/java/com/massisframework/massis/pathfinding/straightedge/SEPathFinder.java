package com.massisframework.massis.pathfinding.straightedge;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.massisframework.massis.model.components.DoorComponent;
import com.massisframework.massis.model.components.FloorReference;
import com.massisframework.massis.model.components.RoomComponent;
import com.massisframework.massis.model.components.ShapeComponent;
import com.massisframework.massis.model.components.impl.ShapeComponentImpl;
import com.massisframework.massis.model.components.impl.StationaryObstacleImpl;
import com.massisframework.massis.model.systems.floor.Floor;
import com.massisframework.massis.model.systems.floor.WallComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationEntityData;
import com.massisframework.massis.util.PathFindingUtils;
import com.massisframework.massis.util.geom.CoordinateHolder;
import com.massisframework.massis.util.geom.KVector;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;
import straightedge.geom.path.MASSISPathFinder;
import straightedge.geom.path.MNodeConnector;
import straightedge.geom.path.PathBlockingObstacleImpl;
import straightedge.geom.path.PathData;
import straightedge.geom.vision.Occluder;

/**
 * A pathfinder based on StraightEdge's pathfinder
 *
 * @author rpax
 *
 */
public class SEPathFinder {

	/**
	 * The amount to expand the walls
	 */
	private static final double BUFFER_AMOUNT = 35;
	/**
	 * Expansion AABB of the rooms
	 */
	private static final int AABB_Expansion = 100;
	/**
	 * Used when buffering, for smoothing points. Its defined as a constant for
	 * clarity
	 */
	private static final int NUM_POINTS_IN_A_QUADRANT = 0;
	/**
	 * The obstacles of this pathfinder
	 */
	private ArrayList<PathBlockingObstacleImpl> stationaryObstacles;
	/**
	 * Connector of the pathfinder noder
	 */
	private MNodeConnector<?> nodeConnector;
	/**
	 * ...
	 */
	private final float maxConnectionDistanceBetweenObstacles = Float.MAX_VALUE;
	/**
	 * MASSIS Pathfinder, based on SE pathfinder
	 */
	private MASSISPathFinder pathFinder;

	/**
	 * Allowed walkable polygons
	 */
	private ArrayList<KPolygon> walkAblePolys;
	/**
	 * Initialization flag
	 */
	private boolean initialized = false;
	private long floorId;
	private SimulationEntityData engine;
	private List<SimulationEntity> walls;
	private ArrayList<SimulationEntity> doors;
	private ArrayList<SimulationEntity> rooms;

	public SEPathFinder(SimulationEntityData engine, long floorId)
	{
		this.floorId = floorId;
		this.engine = engine;
		this.walls = new ArrayList<>();
		this.doors = new ArrayList<>();
		this.rooms = new ArrayList<>();
		this.stationaryObstacles = new ArrayList<PathBlockingObstacleImpl>();
	}

	/**
	 * Recomputes the pathfinder data
	 */
	public void recomputeMesh()
	{

		this.walls.clear();
		this.doors.clear();
		this.rooms.clear();
		this.stationaryObstacles.clear();
		List<KPolygon> obstPolys = new ArrayList<KPolygon>();
		engine.findEntities(WallComponent.class, ShapeComponent.class)
				.forEach(this.walls::add);
		engine.findEntities(DoorComponent.class, ShapeComponent.class)
				.forEach(this.doors::add);
		engine.findEntities(RoomComponent.class, ShapeComponent.class)
				.forEach(this.rooms::add);
		for (SimulationEntity wallEntity : walls)
		{

			if (wallEntity.get(FloorReference.class)
					.getFloorId() != this.floorId)
				continue;
			/*
			 * Substraction of the doors area to the walls area
			 */
			Area area = new Area(
					wallEntity.get(ShapeComponent.class).asShape());
			for (SimulationEntity doorEntity : doors)
			{
				boolean open = doorEntity.get(DoorComponent.class).isOpen();
				KPolygon shape = PathFindingUtils.createKPolygonFromShape(
						doorEntity.get(ShapeComponentImpl.class).getShape());

				if (open)
				{
					area.subtract(new Area(shape));
				}
			}
			/*
			 * It is possible that the area to be splitted in two
			 */
			StationaryObstacleImpl so = new StationaryObstacleImpl();
			for (Area a : PathFindingUtils.getAreas(area))
			{
				KPolygon poly = PathFindingUtils.createKPolygonFromShape(a);
				if (poly == null)
				{
					continue;
				}
				PathBlockingObstacleImpl obst = PathFindingUtils
						.createObstacleFromInnerPolygon(poly, BUFFER_AMOUNT,
								NUM_POINTS_IN_A_QUADRANT);
				so.addObstacle(obst);
				this.stationaryObstacles.add(obst);
			}
			wallEntity.add(so);

		}
		/*
		 * Debugging stuff
		 */
		// int nlines1 = 0;
		// for (KPolygon kPolygon : obstPolys)
		// {
		// nlines1 += KPolygonUtils.getLines(kPolygon).size();
		// }
		//
		// int beforeReduction = obstPolys.size();
		// long start = System.currentTimeMillis();
		// obstPolys = PathFindingUtils.getMinimizedPolygons2(obstPolys);
		// long end = System.currentTimeMillis();
		//
		// int afterReduction = obstPolys.size();
		// int nlines2 = 0;
		// for (KPolygon kPolygon : obstPolys)
		// {
		// nlines2 += KPolygonUtils.getLines(kPolygon).size();
		// }
		/*
		 * Inflation
		 */
		// for (KPolygon kPolygon : obstPolys)
		// {
		// this.stationaryObstacles.add(PathFindingUtils.createObstacleFromInnerPolygon(kPolygon,BUFFER_AMOUNT,
		// NUM_POINTS_IN_A_QUADRANT));
		// }

		// System.out.println("Before/After Reduction : " + beforeReduction +
		// "/"
		// + afterReduction + ",[" + nlines1 + "=>"
		// + nlines2 + "] took " + (end - start) + " ms");
		// for (LowLevelAgent v : floor.getAgents())
		// {
		// if (v.isObstacle() && !v.isDynamic())
		// {
		// this.stationaryObstacles.add(PathFindingUtils
		// .createObstacleFromInnerPolygon(v.getPolygon(),
		// BUFFER_AMOUNT, NUM_POINTS_IN_A_QUADRANT));
		// }
		// }
		// =================================================================================
		// ahora se relacionan los Room con los obstaculos que intersectan con
		// el, dejando el poligono libre de obstaculos
		// es otra cosa totalmente diferente

		walkAblePolys = new ArrayList<>();

		for (SimulationEntity sr : engine.findEntities(RoomComponent.class))
		{
			Area walkAble = new Area(sr.get(ShapeComponent.class).asShape());

			walkAblePolys
					.add(PathFindingUtils.createKPolygonFromShape(walkAble));

		}
		// Connect the obstacles' nodes so that the PathFinder can do its work:
		Floor floor = this.engine.getSimulationEntity(this.floorId)
				.get(Floor.class);
		int scale = 100;
		int bounds = 1000;
		int length = bounds * scale;
		nodeConnector = new MNodeConnector<>(stationaryObstacles,
				maxConnectionDistanceBetweenObstacles, AABB_Expansion,
				-length, length, -length, length);

		// floor.getMinX(), floor.getMaxX(), floor.getMinY(),
		// floor.getMaxY());

		pathFinder = new MASSISPathFinder();
		// ////////

	}

	//

	public KPoint getNearestPointOutsideOfObstacles(KPoint point)
	{
		checkForInitialization();

		// // check that the target point isn't inside any obstacles.
		// // if so, move it.
		KPoint movedPoint = point.copy();
		boolean targetIsInsideObstacle = false;
		int count = 0;
		while (true)
		{
			for (PathBlockingObstacleImpl obst : stationaryObstacles)
			{
				if (obst.getOuterPolygon().contains(movedPoint))
				{

					targetIsInsideObstacle = true;
					KPolygon poly = obst.getOuterPolygon();
					KPoint p = poly.getBoundaryPointClosestTo(movedPoint);
					if (p != null)
					{

						movedPoint.x = p.x;
						movedPoint.y = p.y;

					}

				}
			}
			count++;
			if (targetIsInsideObstacle == false || count >= 3)
			{
				break;
			}
			targetIsInsideObstacle = false;
		}
		return movedPoint;
	}

	/**
	 *
	 * @return the obstacles used in this pathfinder
	 */
	public Iterable<PathBlockingObstacleImpl> getStationaryObstacles()
	{
		checkForInitialization();
		return this.stationaryObstacles;
	}

	/**
	 *
	 * @return the walkable areas
	 */
	public Iterable<KPolygon> getWalkableAreas()
	{
		checkForInitialization();
		return this.walkAblePolys;
	}

	/**
	 * Recomputes mesh if necessary
	 */
	private void checkForInitialization()
	{
		if (!this.initialized)
		{
			this.recomputeMesh();
			this.initialized = true;
		}
	}

	/**
	 * Initializes the pathfinder. <strong>Note:</strong> it will be only
	 * initialized once.
	 */
	public void initialize()
	{
		this.checkForInitialization();
	}

	/**
	 * Finds a path
	 *
	 * @param fromLocation
	 *            start loc
	 * @param toLocation
	 *            goal
	 * @return the path between them, noll if it does not exist
	 */
	public List<CoordinateHolder> findPath(
			CoordinateHolder fromLocation,
			CoordinateHolder toLocation)
	{
		// if (this.floor != fromLocation.getFloor())
		// {
		// callback.onError(
		// FindPathResult.PathFinderErrorReason.DIFFERENT_FLOORS);
		// }
		//
		PathData pathData = this.findPath(
				new KPoint(fromLocation.getX(), fromLocation.getY()),
				new KPoint(toLocation.getX(), toLocation.getY()), null);
		if (pathData.getResult().isError())
		{
			System.err.println(
					"ERROR in pathf: " + pathData.getResult().getMessage());
			return null;
		} else
		{
			return pathData.getPoints().stream().map(kp -> new KVector(kp))
					.collect(Collectors.toList());
		}

		// switch (pathData.getResult())
		// {
		// case ERROR2:
		// callback.onError(
		// FindPathResult.PathFinderErrorReason.INVALID_START_LOCATION);
		// break;
		// case ERROR3:
		// callback.onError(
		// FindPathResult.PathFinderErrorReason.INVALID_END_LOCATION);
		// break;
		// case ERROR4:
		// case NO_RESULT:
		// callback.onError(
		// FindPathResult.PathFinderErrorReason.UNREACHABLE_TARGET);
		// break;
		// case SUCCESS:
		// callback.onSuccess(new Path(pathData.getPoints(), targetTeleport));
		// break;
		// case ERROR1:
		// default:
		// throw new UnsupportedOperationException();
		// }
	}

	private PathData findPath(KPoint from, KPoint to,
			Occluder restrictionPolygon)
	{
		checkForInitialization();

		KPoint pos = getNearestPointOutsideOfObstacles(from);
		KPoint targetAdjusted = getNearestPointOutsideOfObstacles(to);

		PathData pathData = null;

		pathData = pathFinder.calc(pos, targetAdjusted,
				maxConnectionDistanceBetweenObstacles, nodeConnector,
				this.stationaryObstacles, restrictionPolygon);
		return pathData;
	}

}

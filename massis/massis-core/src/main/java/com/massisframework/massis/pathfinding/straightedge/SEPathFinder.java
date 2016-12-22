package com.massisframework.massis.pathfinding.straightedge;

import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.components.Location;
import com.massisframework.massis.model.components.RoomComponent;
import com.massisframework.massis.model.components.building.DoorComponent;
import com.massisframework.massis.model.components.building.MovementCapabilities;
import com.massisframework.massis.model.components.building.ObstacleComponent;
import com.massisframework.massis.model.components.building.ShapeComponent;
import com.massisframework.massis.model.components.building.WallComponent;
import com.massisframework.massis.sim.SimulationEntity;
import com.massisframework.massis.sim.engine.SimulationEngine;
import com.massisframework.massis.util.PathFindingUtils;
import com.massisframework.massis.util.geom.KPolygonUtils;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;
import straightedge.geom.PolygonHolder;
import straightedge.geom.path.MASSISPathFinder;
import straightedge.geom.path.MNodeConnector;
import straightedge.geom.path.PathBlockingObstacle;
import straightedge.geom.path.PathBlockingObstacleImpl;
import straightedge.geom.path.PathData;
import straightedge.geom.vision.Occluder;

/**
 * A pathfinder based on StraightEdge's pathfinder
 *
 * @author rpax
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SEPathFinder implements SimulationPathFinder {

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
	private ArrayList<SimulationEntity> stationaryObstacles;
	private List<PathBlockingObstacle> stationaryObstaclesComponents;
	/**
	 * Connector of the pathfinder noder
	 */
	private MNodeConnector nodeConnector;
	/**
	 * ...
	 */
	private final float maxConnectionDistanceBetweenObstacles = Float.MAX_VALUE;
	/**
	 * MASSIS Pathfinder, based on SE pathfinder
	 */
	private MASSISPathFinder pathFinder;
	/**
	 * IFloor of this pathfinder
	 */
	private final Floor floor;
	/**
	 * Allowed walkable polygons
	 */
	private ArrayList<KPolygon> walkAblePolys;
	/**
	 * Initialization flag
	 */
	private boolean initialized = false;
	private SimulationEngine engine;

	/**
	 * Main constructor. Does not build the pathfinder data, that must be done
	 * with {@link #initialize()}
	 *
	 * @param floor
	 */
	public SEPathFinder(Floor floor, SimulationEngine engine)
	{
		this.floor = floor;
		this.engine = engine;
	}

	private Iterable<SimulationEntity> getWalls()
	{
		return this.engine.getEntitiesFor(WallComponent.class,
				ShapeComponent.class);
	}

	private Iterable<SimulationEntity> getDoors()
	{
		return this.engine.getEntitiesFor(DoorComponent.class,
				ShapeComponent.class);
	}

	/**
	 * Recomputes the pathfinder data
	 */
	private void recomputeMesh()
	{

		this.stationaryObstacles = new ArrayList<SimulationEntity>();
		this.stationaryObstaclesComponents = new ArrayList<>();
		List<KPolygon> obstPolys = new ArrayList<KPolygon>();
		for (SimulationEntity so : getWalls())
		{

			Shape shape = so.get(ShapeComponent.class).getShape();
			/*
			 * Substraction of the doors area to the walls area
			 */
			Area area = new Area(shape);
			for (SimulationEntity se : getDoors())
			{
				DoorComponent d = se.get(DoorComponent.class);
				ShapeComponent sc = se.get(ShapeComponent.class);
				if (d.isOpened())
				{
					area.subtract(new Area(sc.getShape()));
				}
			}
			/*
			 * It is possible that the area to be splitted in two
			 */
			for (Area a : PathFindingUtils.getAreas(area))
			{
				KPolygon poly = PathFindingUtils.createKPolygonFromShape(a);
				if (poly == null)
				{
					continue;
				}
				obstPolys.add(poly);

			}
		}
		/*
		 * Debugging stuff
		 */
		int nlines1 = 0;
		for (KPolygon kPolygon : obstPolys)
		{
			nlines1 += KPolygonUtils.getLines(kPolygon).size();
		}

		int beforeReduction = obstPolys.size();
		long start = System.currentTimeMillis();
		obstPolys = PathFindingUtils.getMinimizedPolygons2(obstPolys);
		long end = System.currentTimeMillis();

		int afterReduction = obstPolys.size();
		int nlines2 = 0;
		for (KPolygon kPolygon : obstPolys)
		{
			nlines2 += KPolygonUtils.getLines(kPolygon).size();
		}
		/*
		 * Inflation
		 */
		for (KPolygon kPolygon : obstPolys)
		{
			this.stationaryObstacles.add(createObstacleEntity(kPolygon));
		}

		System.out.println("Before/After Reduction : " + beforeReduction + "/"
				+ afterReduction + ",[" + nlines1 + "=>"
				+ nlines2 + "] took " + (end - start) + " ms");
		for (SimulationEntity se : engine.getEntitiesFor(Shape.class,
				MovementCapabilities.class))
		{
			MovementCapabilities v = se.get(MovementCapabilities.class);
			if (v.isObstacle() && !v.canMove())
			{
				this.stationaryObstacles.add(
						createObstacleEntity(se.get(ShapeComponent.class)));
			}
		}
		// =================================================================================
		// ahora se relacionan los Room con los obstaculos que intersectan con
		// el, dejando el poligono libre de obstaculos
		// es otra cosa totalmente diferente

		walkAblePolys = new ArrayList<>();

		for (SimulationEntity sr : this.engine.getEntitiesFor(RoomComponent.class))
		{

			Area walkAble = new Area(sr.get(ShapeComponent.class).getShape());

			walkAblePolys
					.add(PathFindingUtils.createKPolygonFromShape(walkAble));

		}
		// Connect the obstacles' nodes so that the PathFinder can do its work:

		nodeConnector = new MNodeConnector(stationaryObstacles,
				maxConnectionDistanceBetweenObstacles, AABB_Expansion,
				floor.getMinX(), floor.getMaxX(), floor.getMinY(),
				floor.getMaxY());

		pathFinder = new MASSISPathFinder();
		// ////////

	}

	private SimulationEntity createObstacleEntity(ShapeComponent s)
	{
		return this.createObstacleEntity(
				KPolygonUtils.createKPolygonFromShape(s.getShape(), true));
	}

	private SimulationEntity createObstacleEntity(PolygonHolder v)
	{
		SimulationEntity entity = this.engine.createEntity();
		PathBlockingObstacleImpl pathBlockingObstacleImpl = PathFindingUtils
				.createObstacleFromInnerPolygon(v.getPolygon(), BUFFER_AMOUNT,
						NUM_POINTS_IN_A_QUADRANT);
		ObstacleComponent oc=engine.newComponent(ObstacleComponent.class);
		oc.setObstacle(pathBlockingObstacleImpl);
		entity.set(oc);
		KPoint center = pathBlockingObstacleImpl.getPolygon().getCenter();
		Location location = this.engine.newComponent(Location.class);
		location.setX(center.x);
		location.setY(center.y);
		location.setFloor(this.floor);
		entity.set(location);
		return entity;
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
			for (SimulationEntity se : stationaryObstacles)
			{
				PathBlockingObstacle obst = se.get(ObstacleComponent.class)
						.getObstacle();
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
	public Iterable<SimulationEntity> getStationaryObstacles()
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
//
//	/**
//	 * Finds a path
//	 *
//	 * @param fromLocation
//	 *            start loc
//	 * @param toLocation
//	 *            goal
//	 * @return the path between them, noll if it does not exist
//	 */
//	public void findPath(Location fromLocation, Location toLocation,
//			TeleportComponent targetTeleport, FindPathResult callback)
//	{
//		if (this.floor != fromLocation.getFloor())
//		{
//			callback.onError(
//					FindPathResult.PathFinderErrorReason.DIFFERENT_FLOORS);
//		}
//
//		PathData pathData = this.findPath(
//				new KPoint(fromLocation.getX(), fromLocation.getY()),
//				new KPoint(toLocation.getX(), toLocation.getY()), null);
//		switch (pathData.getResult())
//		{
//		case ERROR2:
//			callback.onError(
//					FindPathResult.PathFinderErrorReason.INVALID_START_LOCATION);
//			break;
//		case ERROR3:
//			callback.onError(
//					FindPathResult.PathFinderErrorReason.INVALID_END_LOCATION);
//			break;
//		case ERROR4:
//		case NO_RESULT:
//			callback.onError(
//					FindPathResult.PathFinderErrorReason.UNREACHABLE_TARGET);
//			break;
//		case SUCCESS:
//			callback.onSuccess(new Path(pathData.getPoints(), targetTeleport));
//			break;
//		case ERROR1:
//		default:
//			throw new UnsupportedOperationException();
//		}
//	}

	private PathData findPath(KPoint from, KPoint to,
			Occluder restrictionPolygon)
	{
		checkForInitialization();

		KPoint pos = getNearestPointOutsideOfObstacles(from);
		KPoint targetAdjusted = getNearestPointOutsideOfObstacles(to);

		PathData pathData = null;
		this.stationaryObstaclesComponents.clear();

		this.stationaryObstacles.stream()
				.map(se -> se.get(ObstacleComponent.class))
				.map(oc -> oc.getObstacle())
				.forEach(stationaryObstaclesComponents::add);

		pathData = pathFinder.calc(pos, targetAdjusted,
				maxConnectionDistanceBetweenObstacles, nodeConnector,
				this.stationaryObstaclesComponents, restrictionPolygon);
		return pathData;
	}

	@Override
	public List<KPoint> findPath(Location from, Location toLoc)
	{
		return this.findPath(
				new KPoint(from.getX(), from.getY()),
				new KPoint(toLoc.getX(), toLoc.getY()), null).points.stream()
						.collect(Collectors.toList());
	}

}

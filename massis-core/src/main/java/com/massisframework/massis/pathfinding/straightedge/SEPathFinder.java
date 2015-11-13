package com.massisframework.massis.pathfinding.straightedge;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

import com.massisframework.massis.model.agents.DefaultAgent;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.building.SimDoor;
import com.massisframework.massis.model.building.SimRoom;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.managers.movement.Path;
import com.massisframework.massis.util.PathFindingUtils;
import com.massisframework.massis.util.geom.KPolygonUtils;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;
import straightedge.geom.PolygonHolder;
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
@SuppressWarnings(
        {
    "unchecked", "rawtypes"
})
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
     * Floor of this pathfinder
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

    /**
     * Main constructor. Does not build the pathfinder data, that must be done
     * with {@link #initialize()}
     *
     * @param floor
     */
    public SEPathFinder(Floor floor)
    {
        this.floor = floor;
    }

    /**
     * Recomputes the pathfinder data
     */
    private void recomputeMesh()
    {

        this.stationaryObstacles = new ArrayList<PathBlockingObstacleImpl>();
        List<KPolygon> obstPolys = new ArrayList<KPolygon>();
        for (PolygonHolder so : floor.getWalls())
        {
            /*
             * Substraction of the doors area to the walls area
             */
            Area area = new Area(so.getPolygon());
            for (SimDoor d : floor.getDoors())
            {

                if (d.isOpened())
                {
                    area.subtract(new Area(d.getPolygon()));
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
            this.stationaryObstacles.add(PathFindingUtils
                    .createObstacleFromInnerPolygon(kPolygon, BUFFER_AMOUNT,
                    NUM_POINTS_IN_A_QUADRANT));
        }

        System.out.println("Before/After Reduction : " + beforeReduction + "/"
                + afterReduction + ",[" + nlines1 + "=>" + nlines2 + "] took "
                + (end - start) + " ms");
        for (DefaultAgent v : floor.getPeople())
        {
            if (v.isObstacle() && !v.isDynamic())
            {
                this.stationaryObstacles.add(PathFindingUtils
                        .createObstacleFromInnerPolygon(v.getPolygon(),
                        BUFFER_AMOUNT, NUM_POINTS_IN_A_QUADRANT));
            }
        }
        // =================================================================================
        // ahora se relacionan los Room con los obstaculos que intersectan con
        // el, dejando el poligono libre de obstaculos
        // es otra cosa totalmente diferente

        walkAblePolys = new ArrayList<>();

        for (SimRoom sr : this.floor.getRooms())
        {
            Area walkAble = new Area(sr.getPolygon());

            walkAblePolys.add(PathFindingUtils
                    .createKPolygonFromShape(walkAble));

        }
        // Connect the obstacles' nodes so that the PathFinder can do its work:

        nodeConnector = new MNodeConnector(stationaryObstacles,
                maxConnectionDistanceBetweenObstacles, AABB_Expansion,
                floor.minX, floor.maxX, floor.minY, floor.maxY);

        pathFinder = new MASSISPathFinder();
        // ////////

    }

    //
    public ArrayList<KPoint> findPath(KPoint from, KPoint to)
    {
        //get room
//        for (SimRoom sr : this.floor.getRooms())
//        {
//            if (sr.getPolygon().contains(from))
//            {
//                return findPath(from, to, sr);
//            }
//        }
        return findPath(from, to, null);

    }

    public ArrayList<KPoint> findPath(KPoint from, KPoint to,
            Occluder restrictionPolygon)
    {
        checkForInitialization();

        KPoint pos = getNearestPointOutsideOfObstacles(from);
        KPoint targetAdjusted = getNearestPointOutsideOfObstacles(to);

        PathData pathData = null;

        pathData = pathFinder.calc(pos, targetAdjusted,
                maxConnectionDistanceBetweenObstacles, nodeConnector,
                this.stationaryObstacles, restrictionPolygon);

        if (pathData.isError())
        {
            // Let caller solve the problem
            return null;
        }
        return pathData.points;
    }

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
     * @param fromLocation start loc
     * @param toLocation goal
     * @return the path between them, noll if it does not exist
     */
    public Path findPath(Location fromLocation, Location toLocation)
    {
        if (this.floor != fromLocation.getFloor())
        {
            throw new RuntimeException("Floors cannot be different");
        }

        List<KPoint> points = this.findPath(fromLocation.getXY(),
                toLocation.getXY());
        if (points == null)
        {
            return null;
        }
        return new Path(points);

    }
}

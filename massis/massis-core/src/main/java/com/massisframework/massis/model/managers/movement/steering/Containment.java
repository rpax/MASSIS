package com.massisframework.massis.model.managers.movement.steering;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.managers.movement.Steering;
import com.massisframework.massis.util.geom.KLine;
import com.massisframework.massis.util.geom.KPolygonUtils;
import com.massisframework.massis.util.geom.KVector;

import straightedge.geom.AABB;
import straightedge.geom.KPoint;
import straightedge.geom.path.PathBlockingObstacleImpl;
import straightedge.geom.vision.Occluder;

@SuppressWarnings("unused")
public class Containment extends SteeringBehavior {

    public Containment(LowLevelAgent v)
    {
        super(v);

    }

    @Override
    public KVector steer()
    {
        return Steering.stayWithInWalls(v, v.getLocation().getFloor()
                .getContainmentPolygons());
    }

    // Were worse than the other method
    private static KVector stayWithinWalls4(LowLevelAgent agent)
    {
        ArrayList<KLine> roomLines = KPolygonUtils.getLines(agent.getRoom()
                .getPolygon());
        List<KLine> collisionLines = Steering.getCollisionLines(agent, 30);
        for (Iterator<KLine> iterator = collisionLines.iterator(); iterator
                .hasNext();)
        {
            KLine kLine = iterator.next();
            for (Occluder connector : agent.getRoom()
                    .getConnectedRoomConnectors())
            {
                if (connector.getPolygon().intersectsLine(kLine.from, kLine.to))
                {
                    iterator.remove();
                    break;
                }
            }
        }
        double minDist = Double.MAX_VALUE;
        KLine nearestIntersected = null;
        KPoint nearestIntersectionPoint = null;
        for (KLine collisionLine : collisionLines)
        {
            List<KLine> intersectedLines = new ArrayList<>();
            for (KLine roomLine : roomLines)
            {
                if (collisionLine.intersects(roomLine))
                {
                    intersectedLines.add(roomLine);
                }
            }

            for (KLine intersected : intersectedLines)
            {
                KPoint intersectionPoint = KPoint.getLineLineIntersection(
                        intersected.from, intersected.to, collisionLine.from,
                        collisionLine.to);
                double distSq = intersectionPoint
                        .distanceSq(collisionLine.from);
                if (distSq < minDist)
                {
                    minDist = distSq;
                    nearestIntersected = intersected;
                    nearestIntersectionPoint = intersectionPoint;
                }

            }
        }
        if (nearestIntersected != null)
        {
            KVector force = KVector
                    .sub(agent.getXY(), nearestIntersectionPoint).normalize();
            force.mult(agent.getMaxForce() / Math.sqrt(minDist));
            return force;
        } else
        {
            return new KVector();
        }

    }

    private static KVector stayWithinWalls3(LowLevelAgent v)
    {
        if (v.isInDoorArea())
        {
            return new KVector();
        }
        // if (v.isInDoorArea()) return new KVector();
        AABB aabb = v.getPolygon().getAABB().copy();
        int range = 2500;
        aabb.p.x -= range;
        aabb.p.y -= range;
        aabb.p2.x += range;
        aabb.p2.y += range;
        KVector force = new KVector();
        int npoints = 1;
        // KPoint[] boundaries =
        // KPolygonUtils.getBoundaryPointsClosestTo(v.getRoom().getPolygon(),
        // v.getX(), v.getY(), npoints);
        ArrayList<KPoint> boundaries = new ArrayList<>();
        for (PathBlockingObstacleImpl obst : v.getLocation().getFloor()
                .getStationaryObstacles())
        {

            // si lo contine, fuera inmediatamente
            if (obst.getOuterPolygon().contains(v.getXY()))
            {
                KPoint boundary = obst.getOuterPolygon()
                        .getBoundaryPointClosestTo(v.getXY());
                KVector desired = KVector.sub(v.getXY(), boundary);
                desired.normalize();
                desired.mult(-(v.getMaxForce() + 1));

                force.add(desired);
                return force;
            }

        }
        return force;
    }

    public static KVector stayWithInWalls2(LowLevelAgent v, Set<KLine> lines)
    {
        KVector force = new KVector();
        double feelerLength = 100;
        KVector[] feelers = new KVector[]
        {
            // createFeeler(v, 0, feelerLength * 2),
            createFeeler(v, Math.PI / 2, feelerLength),
            createFeeler(v, -Math.PI / 2, feelerLength)
        };

        ArrayList<KPoint> intersections = new ArrayList<>();
        ArrayList<KVector> normals = new ArrayList<>();

        for (KVector feeler : feelers)
        {
            KLine feelerLine = new KLine(v.getXY(), KVector.add(v.getXY(),
                    feeler));
            // double minDistSq = Double.MAX_VALUE;
            KPoint closestIntersection = null;
            KVector closestNormal = null;
            for (KLine crossing : lines)
            {
                if (feelerLine.intersects(crossing))
                {
                    KPoint intersection = feelerLine
                            .getIntersectionPoint(crossing);
                    if (intersection != null)
                    {
                        // double dist = intersection.distanceSq(v.getXY());
                        // if (dist < minDistSq)
                        {
                            // minDistSq = dist;
                            // closestIntersection = intersection;
                            // closestNormal =
                            // crossing.normal().mult(v.getMaxForce()/KPoint.distance(intersection,
                            // v.getXY()));
                            force.add(crossing.normal().mult(
                                    v.getMaxForce()
                                    / KPoint.distance(intersection,
                                    v.getXY())));

                        }
                    }
                }
            }
            // if (closestIntersection != null)
            // {
            // intersections.add(closestIntersection);
            // normals.add(closestNormal);
            // }

        }
        // for (KVector normal : normals)
        // {
        // force.add(normal);
        // }
        return force;
    }

    private static KVector createFeeler(LowLevelAgent v, double angle,
            double length)
    {
        KVector vel = v.getVelocity();
        KVector feeler = new KVector();
        feeler.x = vel.x * Math.cos(angle) - vel.y * Math.sin(angle);
        feeler.y = vel.x * Math.sin(angle) + vel.y * Math.cos(angle);
        feeler.normalize().mult(length);
        return feeler;
    }
}

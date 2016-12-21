package straightedge.geom.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.massisframework.massis.util.field.grid.quadtree.lines.ObstaclesLineGrid;
import com.massisframework.massis.util.geom.KLine;

import straightedge.geom.AABB;
import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;
import straightedge.geom.vision.Occluder;

/**
 * Based on StraightEdge's {@link NodeConnector}
 *
 * @author rpax
 */
public class MNodeConnector<T extends PathBlockingObstacle> {
    // This list is cleared after each method call rather than created anew, to
    // avoid creating new lists all the time.

    ArrayList<ObstAndDist> obstAndDists = new ArrayList<ObstAndDist>();
    ObstaclesLineGrid obstLineGrid;
    List<T> obstaclesToIntersect = new ArrayList<>();
    private final int AABBExpansion;

    // FIXME -> Hacerlo bien y ponerlo en uno solo
    public MNodeConnector(ArrayList<T> obstacles, double maxConnectionDistance,
            int AABBExpansion, int minX, int maxX, int minY, int maxY)
    {
        obstaclesToIntersect.addAll(obstacles);
        this.AABBExpansion = AABBExpansion;
        obstLineGrid = new ObstaclesLineGrid(minX, maxX, minY, maxY);
        obstLineGrid.fill(obstacles);

        for (T obst : obstacles)
        {
            this.addObstacle(obst, obstacles, maxConnectionDistance);
        }

        // Ya tenemos las lineas. Cada vez que aparezca un poligono nuevo vemos
        // cual cabe

    }

    private void reConnectNode(KNodeOfObstacle node,
            double maxConnectionDistance, List<T> obstacles)
    {
        // ct.setEnabled(false);
        // ct.click("startNode.clearConnectedNodes();");
        node.clearConnectedNodes();
        // Test to see if it's ok to ignore this startNode since it's
        // concave (inward-pointing) or it's contained by an obstacle.
        if (node.isConcave()
                || node.getContained() == KNodeOfObstacle.TRUE_VALUE)
        {
            // ct.lastClick();
            return;
        }
        reConnectNodeAfterChecks(node, maxConnectionDistance);
    }

    @SuppressWarnings("unchecked")
    private void reConnectNodeAfterChecks(KNodeOfObstacle node,
            double maxConnectionDistance)
    {
        // make new connections between newObstacle and every other obstacle.
        // To optimise the line-obstacle intersection testing, we'll order the
        // obstacle list
        // by their distance to the startNode, smallest first.
        // These closer obstacles are more likely to intersect any lines from
        // the
        // startNode to the far away obstacle nodes.
        // Dump the obstacles in a new list, along with their distance to the
        // startNode.
        // ct.click("obstAndDists.clear");
        obstAndDists.clear();
        // ct.click("obstAndDists.add");
        KPoint p = node.getPoint();
        for (int n = 0; n < obstaclesToIntersect.size(); n++)
        {
            PathBlockingObstacle obst = obstaclesToIntersect.get(n);
            double dist = node.getPoint().distance(
                    obst.getInnerPolygon().getCenter())
                    - obst.getInnerPolygon().getRadius();
            obstAndDists.add(new ObstAndDist(obst, dist));
        }
        // Sort the list.
        // ct.click("sort");
        Collections.sort(obstAndDists);

        // ct.click("contained check");
        if (node.getContained() == KNodeOfObstacle.UNKNOWN_VALUE)
        {
            // Calculate if the startNode is contained and cache the result.
            // This speeds up this method and the 'makeReachableNodesFor' method
            // significantly.
            for (int i = 0; i < obstAndDists.size(); i++)
            {
                PathBlockingObstacle obst = obstAndDists.get(i).getObst();
                if (obst == node.getObstacle())
                {
                    continue;
                }
                // if (obstAndDists.get(i).getDist() >
                // obst.getInnerPolygon().getRadius()){
                if (obstAndDists.get(i).getDist() > 0)
                {
                    // Break this checking loop since all of the rest of the
                    // obstacles
                    // must be too far away to possibly overlap any points in
                    // testOb1's polygon.
                    // System.out.println("NodeConnector: breaking at i == "+i+" out of obstAndDists.size() == "+obstAndDists.size());
                    break;
                }
                KPolygon poly = obst.getInnerPolygon();
                if (poly.contains(node.getPoint()))
                {
                    node.setContained(KNodeOfObstacle.TRUE_VALUE);
                    break;
                }
            }
            if (node.getContained() == KNodeOfObstacle.TRUE_VALUE)
            {
                // ct.lastClick();
                return;
            } else
            {
                node.setContained(KNodeOfObstacle.FALSE_VALUE);
            }
        }
        // ct.click("connect");
        // Test the startNode for straight lines to nodes in other
        // polygons (including testOb1 itself).
        for (int k = 0; k < obstaclesToIntersect.size(); k++)
        {
            PathBlockingObstacle testOb2 = obstaclesToIntersect.get(k);
            ArrayList<KNodeOfObstacle> testOb2Nodes = testOb2.getNodes();
            // float halfNodeToNode2Dist =
            // startNode.getPoint().distance(testOb2.getInnerPolygon().getCenter())
            // + polygon.getRadius() + testOb2.getInnerPolygon().getRadius();
            NodeLoop:
            for (int m = 0; m < testOb2Nodes.size(); m++)
            {
                // Don't test a 'line' from the exact same points in the same
                // polygon.
                KNodeOfObstacle node2 = testOb2Nodes.get(m);
                // Test to see if it's ok to ignore this startNode since it's
                // concave (inward-pointing) or it's contained by an obstacle.
                if (node2 == node || node2.isConcave()
                        || node2.getContained() == KNodeOfObstacle.TRUE_VALUE)
                {
                    continue;
                }
                KPoint p2 = node2.getPoint();
                double nodeToNode2Dist = p.distance(p2);
                if (nodeToNode2Dist > maxConnectionDistance)
                {
                    continue;
                }

                // Only connect the nodes if the connection will be useful.
                if (isConnectionPossibleAndUseful(node, node.getPointNum(),
                        node.getObstacle().getNodes(), node2, m, testOb2Nodes) == false)
                {
                    continue;
                }

                if (testOb2.getInnerPolygon().intersectsLine(node.getPoint(),
                        node2.getPoint()))
                {
                    continue NodeLoop;
                }

                // Need to test if line from startNode to node2 intersects any
                // obstacles
                // Also test if any startNode is contained in any obstacle.
                for (int n = 0; n < obstAndDists.size(); n++)
                {
                    if (obstAndDists.get(n).getDist() > nodeToNode2Dist)
                    {
                        // Break this checking loop since all of the rest of the
                        // obstacles
                        // must be too far away to possibly overlap the line
                        // from startNode to node2
                        // System.out.println("NodeConnector: breaking at i == "+n+" out of obstAndDists.size() == "+obstAndDists.size());
                        break;
                    }
                    PathBlockingObstacle testOb3 = obstAndDists.get(n)
                            .getObst();
                    KPolygon innerPolygon = testOb3.getInnerPolygon();
                    if (testOb3 == testOb2)
                    {
                        continue;
                    }
                    if (innerPolygon.intersectionPossible(node.getPoint(),
                            node2.getPoint()) == false)
                    {
                        continue;
                    }
                    // Check that startNode is not inside testOb3
                    if (node.getContained() == KNodeOfObstacle.UNKNOWN_VALUE
                            && innerPolygon.contains(node.getPoint()))
                    {
                        continue NodeLoop;
                    }
                    if (innerPolygon.intersectsLine(node.getPoint(),
                            node2.getPoint()))
                    {
                        continue NodeLoop;
                    }
                }
                assert node.getConnectedNodes().contains(node2) == false;
                node.getConnectedNodes().add(node2);
                assert node2.getConnectedNodes().contains(node) == false;
                node2.getConnectedNodes().add(node);
            }
        }
        // ct.click("obstAndDists.clear");
        obstAndDists.clear();
        // ct.lastClick();
    }

    // FIXME es una adaptacion, habria que ponerlo en uno
    private void addObstacle(T obst, ArrayList<T> obstacles,
            double maxConnectionDistance)
    {
        assert obstacles.contains(obst);
        resetObstacleNodes(obst);
        KPolygon poly = obst.getInnerPolygon();
        ArrayList<T> nearByObstacles = obstacles;
        // Any nodes that may be contained need to be marked as so.
        for (T nearByObstacle : nearByObstacles)
        {
            if (nearByObstacle == obst)
            {
                continue;
            }
            for (KNodeOfObstacle node : nearByObstacle.getNodes())
            {
                if (poly.getCenter().distanceSq(node.getPoint()) <= poly
                        .getRadiusSq())
                {
                    boolean contained = poly.contains(node.getPoint());
                    if (contained)
                    {
                        node.setContained(KNodeOfObstacle.TRUE_VALUE);
                        node.clearConnectedNodes();
                    }
                }
            }
        }

        // check if the new obstacle obstructs any startNode connections, and if
        // it does, delete them.
        KPolygon polygon = obst.getInnerPolygon();
        for (int i = 0; i < nearByObstacles.size(); i++)
        {
            PathBlockingObstacle testOb1 = nearByObstacles.get(i);
            if (testOb1 == obst)
            {
                continue;
            }
            for (int j = 0; j < testOb1.getNodes().size(); j++)
            {
                KNodeOfObstacle node = testOb1.getNodes().get(j);
                ArrayList<KNode> reachableNodes = node.getConnectedNodes();
                for (int k = 0; k < reachableNodes.size(); k++)
                {
                    KNode node2 = reachableNodes.get(k);
                    if (polygon.intersectionPossible(node.getPoint(),
                            node2.getPoint())
                            && polygon.intersectsLine(node.getPoint(),
                            node2.getPoint()))
                    {
                        // delete startNode's reachable KNode.
                        reachableNodes.remove(k);
                        // delete node2's reachable KNode too.
                        int index = node2.getConnectedNodes().indexOf(node);
                        node2.getConnectedNodes().remove(index);
                        k--;
                        continue;
                    }
                }
            }
        }
        for (KNodeOfObstacle node : obst.getNodes())
        {
            reConnectNode(node, maxConnectionDistance, obstacles);
        }

    }

    protected int getXIndicator(KPoint p, KPolygon poly)
    {
        int xIndicator;
        double relX = poly.getCenter().x - p.getX();
        if (relX - poly.getRadius() > 0)
        {
            xIndicator = 1;
        } else if (relX + poly.getRadius() < 0)
        {
            xIndicator = -1;
        } else
        {
            xIndicator = 0;
        }
        return xIndicator;
    }

    protected int getYIndicator(KPoint p, KPolygon poly)
    {
        int yIndicator;
        double relY = poly.getCenter().y - p.getY();
        if (relY - poly.getRadius() > 0)
        {
            yIndicator = 1;
        } else if (relY + poly.getRadius() < 0)
        {
            yIndicator = -1;
        } else
        {
            yIndicator = 0;
        }
        return yIndicator;
    }
    // CodeTimer codeTimer = new CodeTimer("makeReachableNodesFor");
    ArrayList<ObstDistAndQuad> obstDistAndQuads = new ArrayList<ObstDistAndQuad>();
    boolean test = true;

    @SuppressWarnings("unchecked")
    public ArrayList<KNode> makeReachableNodesFor(KNode node,
            double maxConnectionDistance, Occluder restrictionPolygon)
    {
        ArrayList<KNode> reachableNodes = new ArrayList<>();
        AABB restrictionAABB = null;
        if (restrictionPolygon != null)
        {
            restrictionAABB = restrictionPolygon.getPolygon().getAABB();
            restrictionAABB.p.x -= AABBExpansion;
            restrictionAABB.p.y -= AABBExpansion;
            restrictionAABB.p2.x += AABBExpansion;
            restrictionAABB.p2.y += AABBExpansion;
            maxConnectionDistance = restrictionAABB.getHeight()
                    + restrictionAABB.getWidth();
        }

        // codeTimer.click("clear");
        // To optimise the line-obstacle intersection testing, order the
        // obstacle list
        // by their distance to the startNode, smallest first.
        // These closer obstacles are more likely to intersect any lines from
        // the
        // startNode to the far away obstacle nodes.
        // Dump the obstacles in a new list, along with their distance to the
        // startNode.
        obstDistAndQuads.clear();
        // codeTimer.click("add");
        // add the obstacles to obstDistAndQuads
        KPoint p = node.getPoint();
        for (int n = 0; n < obstaclesToIntersect.size(); n++)
        {
            KPolygon poly = obstaclesToIntersect.get(n).getInnerPolygon();
            
            if (restrictionAABB!=null && !poly.getAABB().intersects(restrictionAABB))
            {
                continue;
            }
            double distEyeToCenterLessRadius = p.distance(poly.getCenter())
                    - poly.getRadius();
            // Note that distCenterToEyeLessCircBound can be negative.
            double distEyeToCenterLessRadiusSqSigned = distEyeToCenterLessRadius
                    * distEyeToCenterLessRadius;
            if (distEyeToCenterLessRadius < 0)
            {
                // to preserve the sign of the original number:
                distEyeToCenterLessRadiusSqSigned *= -1;
            }
            int xIndicator = getXIndicator(p, poly);
            int yIndicator = getYIndicator(p, poly);
            ObstDistAndQuad obstDistAndQuad = new ObstDistAndQuad(
                    obstaclesToIntersect.get(n),
                    distEyeToCenterLessRadiusSqSigned, xIndicator, yIndicator);
            obstDistAndQuads.add(obstDistAndQuad);
        }
        // codeTimer.click("sort");
        // Sort the list.
        Collections.sort(obstDistAndQuads);
        // codeTimer.click("connect");
        // ========================================================
        if (test)
        {
            for (int k = 0; k < obstDistAndQuads.size(); k++)
            {
                ObstDistAndQuad obstDistAndQuad = obstDistAndQuads.get(k);
                PathBlockingObstacle testOb2 = obstDistAndQuad.getObst();
                ArrayList<KNodeOfObstacle> testOb2Nodes = testOb2.getNodes();
                for (int m = 0; m < testOb2Nodes.size(); m++)
                {
                    KNodeOfObstacle node2 = testOb2Nodes.get(m);
                    if (node2 == node)
                    {
                        continue;
                    }
                    if (node2.isConcave()
                            || node2.getContained() == KNodeOfObstacle.TRUE_VALUE)
                    {
                        continue;
                    }
                    if (restrictionAABB != null && !restrictionAABB.contains(
                            node2.getPoint()))
                    {
                        continue;
                    }
                    if (isConnectionPossibleAndUseful(node, node2, m,
                            testOb2Nodes) == false)
                    {
                        continue;
                    }
                    if (this.obstLineGrid.lineIntersectedBy(new KLine(node
                            .getPoint(), node2.getPoint())) == null)
                    {
                        reachableNodes.add(node2);
                    }
                }
            }
            return reachableNodes;
        }

        // ========================================================
        double maxConnectionDistanceSq = maxConnectionDistance
                * maxConnectionDistance;

        // Test for straight lines between the startNode and nodes of obstacles
        // that don't intersect any obstacles.

        for (int k = 0; k < obstDistAndQuads.size(); k++)
        {
            ObstDistAndQuad obstDistAndQuad = obstDistAndQuads.get(k);
            PathBlockingObstacle testOb2 = obstDistAndQuad.getObst();
            ArrayList<KNodeOfObstacle> testOb2Nodes = testOb2.getNodes();

            NodeLoop:
            for (int m = 0; m < testOb2Nodes.size(); m++)
            {
                KNodeOfObstacle node2 = testOb2Nodes.get(m);
                // Ignore this startNode since it's concave (inward-pointing)
                if (node2.isConcave()
                        || node2.getContained() == KNodeOfObstacle.TRUE_VALUE)
                {
                    continue;
                }
                // =================================================================================
                // if (restrictionPolygon != null
                // && !restrictionAABB.contains(node2.getPoint()))
                // {
                // continue;
                // }

                // =================================================================================
                if (isConnectionPossibleAndUseful(node, node2, m, testOb2Nodes) == false)
                {
                    continue;
                }

                KPoint p2 = node2.getPoint();

                // Need to test if line from startNode to node2 intersects any
                // obstacles (including testOb2 itself).
                // Should check closest obst first, and obst whose points were
                // found to be reachable
                double nodeToNode2DistSq = p.distanceSq(p2);
                if (nodeToNode2DistSq > maxConnectionDistanceSq)
                {
                    continue;
                }

                if (testOb2.getInnerPolygon().intersectsLine(p, p2))
                {
                    continue NodeLoop;
                }

                for (int n = 0; n < obstDistAndQuads.size(); n++)
                {
                    ObstDistAndQuad obstDistAndQuad2 = obstDistAndQuads.get(n);
                    PathBlockingObstacle testOb3 = obstDistAndQuad2.getObst();
                    KPolygon innerPolygon = testOb3.getInnerPolygon();
                    if (testOb3 == testOb2)
                    {
                        continue;
                    }
                    // Test if testOb3.getInnerPolygon() intersects the line
                    // from startNode to node2
                    if (obstDistAndQuads.get(n)
                            .getDistNodeToCenterLessRadiusSqSigned() > nodeToNode2DistSq)
                    {
                        // Break this checking loop since all of the rest of the
                        // obstacles
                        // must be too far away to possibly overlap the line
                        // from startNode to node2
                        break;
                    }
                    if (obstDistAndQuad.getXIndicator()
                            * obstDistAndQuad2.getXIndicator() == -1
                            || obstDistAndQuad.getYIndicator()
                            * obstDistAndQuad2.getYIndicator() == -1)
                    {
                        continue;
                    }
                    if (innerPolygon.intersectionPossible(p, p2) == false)
                    {
                        continue;
                    }
                    // Check that node2 is not inside testOb3
                    if (node2.getContained() == KNodeOfObstacle.UNKNOWN_VALUE
                            && innerPolygon.contains(p2))
                    {
                        continue NodeLoop;
                    }
                    if (innerPolygon.intersectsLine(p, p2))
                    {
                        continue NodeLoop;
                    }
                }
                assert (reachableNodes.contains(node2) == false);
                reachableNodes.add(node2);
            }
        }

        obstDistAndQuads.clear();
        // codeTimer.lastClick();
        return reachableNodes;
    }
    double smallAmount = 0.0001;

    private boolean isConnectionPossibleAndUseful(KNode node,
            KNodeOfObstacle node2, int node2PointNum,
            ArrayList<KNodeOfObstacle> node2List)
    {
        KPoint p = node.getPoint();
        KPoint p2 = node2.getPoint();
        // test if startNode is in the reject region of node2's obstacle
        {
            // Only connect the nodes if the connection will be useful.
            // See the comment in the method makeReachableNodes for a full
            // explanation.
            KNode node2Minus = node2List.get(node2PointNum - 1 < 0 ? node2List
                    .size() - 1 : node2PointNum - 1);
            KNode node2Plus = node2List.get(node2PointNum + 1 >= node2List
                    .size() ? 0 : node2PointNum + 1);
            KPoint p2Minus = node2Minus.getPoint();
            KPoint p2Plus = node2Plus.getPoint();

            // double p2MinusToP2RCCW = p.c(p2Minus, p2);
            double p2LessP2MinusX = p2.x - p2Minus.x;
            double p2LessP2MinusY = p2.y - p2Minus.y;
            double pLessP2MinusX = p.x - p2Minus.x;
            double pLessP2MinusY = p.y - p2Minus.y;
            double p2MinusToP2RCCW = pLessP2MinusY * p2LessP2MinusX
                    - pLessP2MinusX * p2LessP2MinusY;

            // double p2ToP2PlusRCCW = p.c(p2, p2Plus);
            double pLessP2X = p.x - p2.x;
            double pLessP2Y = p.y - p2.y;
            double p2PlusLessP2X = p2Plus.x - p2.x;
            double p2PlusLessP2Y = p2Plus.y - p2.y;
            double p2ToP2PlusRCCW = pLessP2Y * p2PlusLessP2X - pLessP2X
                    * p2PlusLessP2Y;

            if (p2MinusToP2RCCW * p2ToP2PlusRCCW > 0)
            {
                // System.out.println(this.getClass().getSimpleName()+": p2MinusToP2RCCWInt * p2ToP2PlusRCCWInt == 1");
                // System.out.println(this.getClass().getSimpleName()+": p2MinusToP2RCCW == "+p2MinusToP2RCCW);
                // System.out.println(this.getClass().getSimpleName()+": p2ToP2PlusRCCW == "+p2ToP2PlusRCCW);
                // System.out.println(this.getClass().getSimpleName()+": p2Minus == "+p2Minus);
                // System.out.println(this.getClass().getSimpleName()+": p2 == "+p2);
                // System.out.println(this.getClass().getSimpleName()+": p2Plus == "+p2Plus);
                // System.out.println(this.getClass().getSimpleName()+": p == "+p);
                // To avoid floating point error problems we should only return
                // false
                // if p is well away from the lines. If it's close, then return
                // true just to be safe. Returning false when the connection is
                // actually useful is a much bigger problem than returning true
                // and sacrificing some performance.
                // double p2MinusToP2LineDistSq = p.ptLineDistSq(p2Minus, p2);
                // if (p2MinusToP2LineDistSq < smallAmount){
                // return true;
                // }
                {
                    double dotprod = pLessP2MinusX * p2LessP2MinusX
                            + pLessP2MinusY * p2LessP2MinusY;
                    // dotprod is the length of the px,py vector
                    // projected on the x1,y1=>x2,y2 vector times the
                    // length of the x1,y1=>x2,y2 vector
                    double projlenSq = dotprod
                            * dotprod
                            / (p2LessP2MinusX * p2LessP2MinusX + p2LessP2MinusY
                            * p2LessP2MinusY);
                    // Distance to line is now the length of the relative point
                    // vector minus the length of its projection onto the line
                    double p2MinusToP2LineDistSq = pLessP2MinusX
                            * pLessP2MinusX + pLessP2MinusY * pLessP2MinusY
                            - projlenSq;
                    if (p2MinusToP2LineDistSq < smallAmount)
                    {
                        return true;
                    }
                }
                // double p2ToP2PlusLineDistSq = p.ptLineDistSq(p2, p2Plus);
                // if (p2ToP2PlusLineDistSq < smallAmount){
                // return true;
                // }
                {
                    double dotprod = pLessP2X * p2PlusLessP2X + pLessP2Y
                            * p2PlusLessP2Y;
                    // dotprod is the length of the px,py vector
                    // projected on the x1,y1=>x2,y2 vector times the
                    // length of the x1,y1=>x2,y2 vector
                    double projlenSq = dotprod
                            * dotprod
                            / (p2PlusLessP2X * p2PlusLessP2X + p2PlusLessP2Y
                            * p2PlusLessP2Y);
                    // Distance to line is now the length of the relative point
                    // vector minus the length of its projection onto the line
                    double p2ToP2PlusLineDistSq = pLessP2X * pLessP2X
                            + pLessP2Y * pLessP2Y - projlenSq;
                    if (p2ToP2PlusLineDistSq < smallAmount)
                    {
                        return true;
                    }
                }
                // Since p is anti-clockwise to both lines p2MinusToP2 and
                // p2ToP2Plus
                // (or it is clockwise to both lines) then the connection betwen
                // them will not be useful so return .
                return false;
            }
        }
        return true;
    }

    protected boolean isConnectionPossibleAndUseful(KNodeOfObstacle node,
            KNodeOfObstacle node2)
    {
        return isConnectionPossibleAndUseful(node, node.getPointNum(), node
                .getObstacle().getNodes(), node2, node2.getPointNum(), node2
                .getObstacle().getNodes());
    }

    protected boolean isConnectionPossibleAndUseful(KNodeOfObstacle node,
            int nodePointNum, ArrayList<KNodeOfObstacle> nodeList,
            KNodeOfObstacle node2, int node2PointNum,
            ArrayList<KNodeOfObstacle> node2List)
    {
        KPoint p = node.getPoint();
        KPoint p2 = node2.getPoint();
        // test if startNode is in the reject region of node2's obstacle
        {
            // Only connect the nodes if the connection will be useful.
            // See the comment in the method makeReachableNodes for a full
            // explanation.
            KNode node2Minus = node2List.get(node2PointNum - 1 < 0 ? node2List
                    .size() - 1 : node2PointNum - 1);
            KNode node2Plus = node2List.get(node2PointNum + 1 >= node2List
                    .size() ? 0 : node2PointNum + 1);
            KPoint p2Minus = node2Minus.getPoint();
            KPoint p2Plus = node2Plus.getPoint();

            double p2MinusToP2RCCW = p.relCCWDouble(p2Minus, p2);
            double p2ToP2PlusRCCW = p.relCCWDouble(p2, p2Plus);
            if (p2MinusToP2RCCW * p2ToP2PlusRCCW > 0)
            {
                // To avoid floating point error problems we should only return
                // false
                // if p is well away from the lines. If it's close, then return
                // true just to be safe. Returning false when the connection is
                // actually useful is a much bigger problem than returning true
                // and sacrificing some performance.
                double p2MinusToP2LineDistSq = p.ptLineDistSq(p2Minus, p2);
                if (p2MinusToP2LineDistSq < smallAmount)
                {
                    return true;
                }
                double p2ToP2PlusLineDistSq = p.ptLineDistSq(p2, p2Plus);
                if (p2ToP2PlusLineDistSq < smallAmount)
                {
                    return true;
                }
                // Since p is anti-clockwise to both lines p2MinusToP2 and
                // p2ToP2Plus
                // (or it is clockwise to both lines) then the connection betwen
                // them will not be useful so return .
                return false;
            }
        }
        // test if node2 is in the reject region of node1's obstacle
        {
            // Only connect the nodes if the connection will be useful.
            // See the comment in the method makeReachableNodes for a full
            // explanation.
            KNode nodeMinus = nodeList.get(nodePointNum - 1 < 0 ? nodeList
                    .size() - 1 : nodePointNum - 1);
            KNode nodePlus = nodeList
                    .get(nodePointNum + 1 >= nodeList.size() ? 0
                    : nodePointNum + 1);
            KPoint pMinus = nodeMinus.getPoint();
            // KPoint p2 = node2.getPoint();
            KPoint pPlus = nodePlus.getPoint();
            double pMinusToPRCCW = p2.relCCWDouble(pMinus, p);
            double pToPPlusRCCW = p2.relCCWDouble(p, pPlus);
            if (pMinusToPRCCW * pToPPlusRCCW > 0)
            {
                // To avoid floating point error problems we should only return
                // false
                // if p is well away from the lines. If it's close, then return
                // true just to be safe. Returning false when the connection is
                // actually useful is a much bigger problem than returning true
                // and sacrificing some performance.
                double pMinusToPLineDistSq = p2.ptLineDistSq(pMinus, p);
                if (pMinusToPLineDistSq < smallAmount)
                {
                    return true;
                }
                double pToPPlusLineDistSq = p2.ptLineDistSq(p, pPlus);
                if (pToPPlusLineDistSq < smallAmount)
                {
                    return true;
                }
                // Since p is anti-clockwise to both lines p2MinusToP2 and
                // p2ToP2Plus
                // (or it is clockwise to both lines) then the connection betwen
                // them will not be useful so return .
                return false;
            }
        }
        return true;
    }

    public void resetObstacleNodes(ArrayList<T> obstacles)
    {
        for (int i = 0; i < obstacles.size(); i++)
        {
            resetObstacleNodes(obstacles.get(i));
        }
    }

    public void resetObstacleNodes(T obstacle)
    {
        for (int j = 0; j < obstacle.getNodes().size(); j++)
        {
            KNodeOfObstacle node = obstacle.getNodes().get(j);
            node.getConnectedNodes().clear();
            node.getTempConnectedNodes().clear();
            node.resetContainedToUnknown();
        }
    }
}

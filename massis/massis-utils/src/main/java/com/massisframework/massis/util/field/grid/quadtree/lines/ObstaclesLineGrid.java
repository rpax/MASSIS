package com.massisframework.massis.util.field.grid.quadtree.lines;

import static com.massisframework.massis.util.field.grid.quadtree.lines.LineQTNode.LEAF_NODE;

import java.util.ArrayList;
import java.util.Set;

import com.massisframework.massis.util.geom.CoordinateHolder;
import com.massisframework.massis.util.geom.KLine;

import straightedge.geom.AABB;
import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;
import straightedge.geom.path.KNodeOfObstacle;
import straightedge.geom.path.PathBlockingObstacle;

public class ObstaclesLineGrid {

    protected final int minX, maxX, minY, maxY;
    protected LineQTNode tree;
    private int KLINE_MAX_INDEX = 0;

    public ObstaclesLineGrid(int minX, int maxX, int minY, int maxY)
    {
        this.minX = minX - 50;
        this.maxX = maxX + 50;
        this.minY = minY - 50;
        this.maxY = maxY + 50;
        // TODO dependiendo de como inflado este
        this.tree = new LineQTNode(minX, minY, maxX - minX, maxY - minY,
                LEAF_NODE, null);

    }

    public void fill(Iterable<? extends PathBlockingObstacle> obstacles)
    {

        for (final PathBlockingObstacle obst : obstacles)
        {

            final ArrayList<KPoint> points = obst.getInnerPolygon().getPoints();
            for (int i = 0; i < points.size() - 1; i++)
            {
                final KLineIndexable line = new KLineIndexable(points.get(i),
                        points.get(i + 1));
                this.tree.insert(line);

            }
            final KLineIndexable line = new KLineIndexable(
                    points.get(points.size() - 1), points.get(0));
            this.tree.insert(line);

        }

    }

    public static class KNodeContainer implements CoordinateHolder {

        private final KNodeOfObstacle node;

        public KNodeContainer(KNodeOfObstacle node)
        {
            this.node = node;
        }

        @Override
        public double getX()
        {
            return this.node.getPoint().getX();
        }

        @Override
        public double getY()
        {
            return this.node.getPoint().getY();
        }

        public KNodeOfObstacle getNode()
        {
            return this.node;
        }
    }

    public KLine lineIntersectedBy(final KLine line)
    {
        if (this.tree.nodeSquareIntersects(line))
        {
            return this.tree.getIntersectedLine(line);
        } else
        {
            return null;
        }
    }

    public Set<KLine> linesIntersecting(final AABB aabb)
    {
        return this.tree.linesIntersecting(aabb);
    }

    public class KLineIndexable extends KLine {

        public int id;

        public KLineIndexable(KPoint from, KPoint to)
        {
            super(from, to);
            this.id = ObstaclesLineGrid.this.KLINE_MAX_INDEX++;
        }

        @Override
        public String toString()
        {
            return "line#" + this.id;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + getOuterType().hashCode();
            result = prime * result + this.id;
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (!super.equals(obj))
            {
                return false;
            }
            if (!(obj instanceof KLineIndexable))
            {
                return false;
            }
            final KLineIndexable other = (KLineIndexable) obj;
            if (!getOuterType().equals(other.getOuterType()))
            {
                return false;
            }
            if (this.id != other.id)
            {
                return false;
            }
            return true;
        }

        private ObstaclesLineGrid getOuterType()
        {
            return ObstaclesLineGrid.this;
        }
    }

    public Iterable<KPolygon> getNodeRectangles()
    {
        return this.tree.getRectangles();
    }
}

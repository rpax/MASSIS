package com.massisframework.massis.util.field.grid.quadtree.lines;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.massisframework.massis.util.geom.KLine;
import com.massisframework.massis.util.geom.KVector;

import straightedge.geom.AABB;
import straightedge.geom.KPolygon;

/**
 * Quadtree of lines
 *
 * @author rpax
 *
 */
public class LineQTNode {

    /**
     * Default size of the buckets
     */
    public static final int DEFAULT_BUCKET_SIZE = 4;
    // Node type constants
    /**
     * Parent Node: It has children
     */
    public static final byte PARENT_NODE = 0;
    /**
     * Leaf: Contains one or more objects
     */
    public static final byte LEAF_NODE = 1;
    /**
     * Child coords
     */
    // protected static final int NW = 0, NE = 1, SW = 2, SE = 3;
    protected LineQTNode child_NE, child_SE, child_SW, child_NW;
    // protected QTNode4<E>[] children;
    /**
     * x coordinate of this node
     */
    public int node_x;
    /**
     * y coordinate of this node
     */
    public int node_y;
    /**
     * width of this node
     */
    public int w;
    /**
     * height of this node
     */
    public int h;
    /**
     * Type of this node. It can be:<br/> {@code POINTER} has no data in it. It
     * has 4 children<br/> {@code LEAF} contains an object<br/> {@code EMPTY}
     * Does not represent anything<br/>
     *
     */
    public byte type;
    /**
     * The parent of this node. {@code null } if it is the root.
     */
    protected LineQTNode parent;
    /**
     * Maximum bucket size
     */
    protected int maxBucketSize;
    /**
     * Bucket
     */
    protected ArrayList<KLine> bucket;
    protected int bucketSize = 0;
    public final int id;
    private final int maxLevel = 8;
    private static int MAX_N_ID = 0;

    public LineQTNode(int node_x, int node_y, int w, int h, byte type,
            LineQTNode parent, int maxBucketSize)
    {
        this.node_x = node_x;
        this.node_y = node_y;
        this.w = w;
        this.h = h;
        this.type = type;
        this.parent = parent;
        this.maxBucketSize = maxBucketSize;
        this.bucket = new ArrayList<>(maxBucketSize);
        this.id = MAX_N_ID++;

    }

    public LineQTNode(int node_x, int node_y, int w, int h, byte type,
            LineQTNode parent)
    {
        this.node_x = node_x;
        this.node_y = node_y;
        this.w = w;
        this.h = h;
        this.type = type;
        this.parent = parent;
        this.maxBucketSize = DEFAULT_BUCKET_SIZE;
        this.bucket = new ArrayList<>(maxBucketSize);
        this.id = MAX_N_ID++;

    }

    public void insert(KLine line)
    {
        this.insert(line, 0);
    }

    public void insert(KLine line, int level)
    {

        if (this.type == LEAF_NODE)
        {
            boolean sharepoint = false;
            for (KLine lineInBucket : this.bucket)
            {
                if (sharePoint(lineInBucket, line))
                {
                    sharepoint = true;
                    break;
                }
            }

            this.bucket.add(line);
            if (!sharepoint)
            {
                this.bucketSize++;
            }

            if (this.bucketSize > this.maxBucketSize && level < maxLevel)
            {
                // Split this node.
                this.type = PARENT_NODE;
                ArrayList<KLine> tmp = new ArrayList<>(bucket);
                this.bucket = new ArrayList<>();
                this.bucketSize = 0;
                for (KLine obj : tmp)
                {
                    // insertamos a partir de este nodo
                    this.insert(obj, level);

                }
                // clean the bucket

            } else
            {
                // nothing
            }

            //
        } else if (this.type == PARENT_NODE)
        {

            // //System.err.println("PARENT_NODE : "+this.id);
            final int hw = this.w >> 1;
            final int hh = this.h >> 1;
            final int mx = node_x + hw;
            final int my = node_y + hh;
            /**
             * @formatter:off x mx y	╔══════════╦══════════╗ ║ child_NW ║
             * child_NE ║ my	╠══════════╬══════════╣ ║ child_SW ║ child_SE ║
             * ╚══════════╩══════════╝
             */
            this.child_NW = getSafeChild(this.child_NW, node_x, node_y, hw, hh);
            this.child_NE = getSafeChild(this.child_NE, mx, node_y, hw, hh);
            this.child_SW = getSafeChild(this.child_SW, node_x, my, hw, hh);
            this.child_SE = getSafeChild(this.child_SE, mx, my, hw, hh);
            if (this.child_NW.nodeSquareIntersects(line))
            {
                //System.err.println("Inserting ["+line+"]: into child_NW"+("("+child_NW.id+")"));
                child_NW.insert(line, level + 1);
            }
            if (this.child_NE.nodeSquareIntersects(line))
            {
                //System.err.println("Inserting ["+line+"]: into child_NE"+("("+child_NE.id+")"));
                child_NE.insert(line, level + 1);
            }
            if (this.child_SW.nodeSquareIntersects(line))
            {
                //System.err.println("Inserting ["+line+"]: into child_SW"+("("+child_SW.id+")"));
                child_SW.insert(line, level + 1);
            }
            if (this.child_SE.nodeSquareIntersects(line))
            {
                //System.err.println("Inserting ["+line+"]: into child_SE"+("("+child_SE.id+")"));
                child_SE.insert(line, level + 1);
            }

        }
        /**
         * @formatter:on
         */
    }

    public KLine getIntersectedLine(KLine line)
    {


        if (this.type == PARENT_NODE)
        {
            KLine intersected = null;
            if (this.child_NW.nodeSquareIntersects(line))
            {
                intersected = this.child_NW.getIntersectedLine(line);
            }
            if (intersected == null && this.child_NE.nodeSquareIntersects(line))
            {
                intersected = this.child_NE.getIntersectedLine(line);
            }
            if (intersected == null && this.child_SW.nodeSquareIntersects(line))
            {
                intersected = this.child_SW.getIntersectedLine(line);
            }
            if (intersected == null && this.child_SE.nodeSquareIntersects(line))
            {
                intersected = this.child_SE.getIntersectedLine(line);
            }
            return intersected;
        } else
        {
            for (KLine containedLine : this.bucket)
            {
                if (lineIntersects(line, containedLine))
                {
                    return containedLine;

                }
            }
        }
        return null;
    }

    /**
     * Returns the node passed as parameter, if it is not {@code null}.<br/>
     * if it is {
     *
     * @null}, return a new one.
     *
     * @param node the node to be returned if it is not null.
     * @return
     */
    protected LineQTNode getSafeChild(LineQTNode node, int x, int y, int w,
            int h)
    {
        return node == null ? new LineQTNode(x, y, w, h, LEAF_NODE, this,
                this.maxBucketSize) : node;
    }

    public ArrayList<KPolygon> getRectangles()
    {
        ArrayList<KPolygon> rects = new ArrayList<>();
        getRectangles(rects);
        return rects;
    }

    protected void getRectangles(ArrayList<KPolygon> polys)
    {
        if (this.type == LEAF_NODE)
        {
            polys.add(KPolygon.createRect(this.node_x, this.node_y, this.node_x
                    + this.w, this.node_y + this.h));
        } else
        {
            if (this.child_NE != null)
            {
                this.child_NE.getRectangles(polys);
            }
            if (this.child_NW != null)
            {
                this.child_NW.getRectangles(polys);
            }
            if (this.child_SE != null)
            {
                this.child_SE.getRectangles(polys);
            }
            if (this.child_SW != null)
            {
                this.child_SW.getRectangles(polys);
            }
        }

    }

    private boolean rangeIntersects(AABB aabb)
    {
        return !(node_x >= aabb.p2.x || node_x + w <= aabb.p.x
                || node_y >= aabb.p2.y || node_y + w <= aabb.p.y);
    }
    // ///////////////////////////////
    private static final int INSIDE = 0; // 0000
    private static final int LEFT = 1; // 0001
    private static final int RIGHT = 2; // 0010
    private static final int BOTTOM = 4; // 0100
    private static final int TOP = 8; // 1000

    private int computeOutCode(int x, int y)
    {
        int code;

        code = INSIDE; // initialised as being inside of clip window

        if (x < this.node_x) // to the left of clip window
        {
            code |= LEFT;
        } else if (x > this.node_x + this.w) // to the right of clip window
        {
            code |= RIGHT;
        }
        if (y < this.node_y) // below the clip window
        {
            code |= BOTTOM;
        } else if (y > this.node_y + this.h) // above the clip window
        {
            code |= TOP;
        }

        return code;
    }

    // Cohen–Sutherland clipping algorithm clips a line from
    // P0 = (x0, y0) to P1 = (x1, y1) against a rectangle with
    // diagonal from (xmin, ymin) to (xmax, ymax).
    public boolean nodeSquareIntersects(KLine line)
    {
        // compute outcodes for P0, P1, and whatever point lies outside the
        // clip rectangle
        int x0 = (int) line.from.x;
        int y0 = (int) line.from.y;
        int x1 = (int) line.to.x;
        int y1 = (int) line.to.y;
        int outcode0 = computeOutCode(x0, y0);
        int outcode1 = computeOutCode(x1, y1);
        final int ymax = this.node_y + this.h;
        final int xmax = this.node_x + this.w;
        final int ymin = this.node_y;
        final int xmin = this.node_x;
        boolean accept = false;
        while (true)
        {
            if (!((outcode0 | outcode1) != 0))
            { // Bitwise OR is 0. Trivially accept and get out of loop
                accept = true;
                break;
            } else if ((outcode0 & outcode1) != 0)
            { // Bitwise AND is not 0. Trivially reject and get out of loop
                break;
            } else
            {
                // failed both tests, so calculate the line segment to clip
                // from an outside point to an intersection with clip edge
                int x = 0, y = 0;

                // At least one endpoint is outside the clip rectangle; pick
                // it.
                int outcodeOut = (outcode0 != 0) ? outcode0 : outcode1;

                // Now find the intersection point;
                // use formulas y = y0 + slope * (x - x0), x = x0 + (1 /
                // slope) * (y - y0)
                if ((outcodeOut & TOP) != 0)
                { // point is above the clip rectangle
                    x = x0 + (x1 - x0) * (ymax - y0) / (y1 - y0);
                    y = ymax;
                } else if ((outcodeOut & BOTTOM) != 0)
                { // point is below the clip rectangle
                    x = x0 + (x1 - x0) * (ymin - y0) / (y1 - y0);
                    y = ymin;
                } else if ((outcodeOut & RIGHT) != 0)
                { // point is to the right of clip rectangle
                    y = y0 + (y1 - y0) * (xmax - x0) / (x1 - x0);
                    x = xmax;
                } else if ((outcodeOut & LEFT) != 0)
                { // point is to the left of clip rectangle
                    y = y0 + (y1 - y0) * (xmin - x0) / (x1 - x0);
                    x = xmin;
                }

                // Now we move outside point to intersection point to clip
                // and get ready for next pass.
                if (outcodeOut == outcode0)
                {
                    x0 = x;
                    y0 = y;
                    outcode0 = computeOutCode(x0, y0);
                } else
                {
                    x1 = x;
                    y1 = y;
                    outcode1 = computeOutCode(x1, y1);
                }
            }
        }
        return accept;
    }

    private static boolean lineIntersects(KLine line1, KLine line2)
    {
        // if (sharePoint(line1, line2))
        // return false;

        if (line1.containsPoint(line2.from, 1))
        {
            return true;
        }
        if (line1.containsPoint(line2.to, 1))
        {
            return true;
        }
        if (line2.containsPoint(line1.from, 1))
        {
            return true;
        }
        if (line2.containsPoint(line1.to, 1))
        {
            return true;
        }

        return line1.intersects(line2);
    }

    private static boolean sharePoint(KLine line1, KLine line2)
    {
        return ( // check if endpoints are the same
                KVector.equals(line1.from, line2.from)
                || KVector.equals(line1.from, line2.to)
                || KVector.equals(line1.to, line2.from) || KVector.equals(
                line1.to, line2.to));
    }

    public Set<KLine> linesIntersecting(AABB aabb)
    {
        HashSet<KLine> lines = new HashSet<>();

        this.linesIntersecting(aabb, lines);

        return lines;
    }

    private void linesIntersecting(AABB aabb, HashSet<KLine> set)
    {

        if (this.rangeIntersects(aabb))
        {

            if (this.type == PARENT_NODE)
            {
                if (this.child_NE != null)
                {
                    this.child_NE.linesIntersecting(aabb, set);
                }
                if (this.child_NW != null)
                {
                    this.child_NW.linesIntersecting(aabb, set);
                }
                if (this.child_SE != null)
                {
                    this.child_SE.linesIntersecting(aabb, set);
                }
                if (this.child_SW != null)
                {
                    this.child_SW.linesIntersecting(aabb, set);
                }
            } else
            {
                for (KLine line : this.bucket)
                {


                    set.add(line);

                }

            }
        }
    }
}

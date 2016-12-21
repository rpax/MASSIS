package com.massisframework.massis.model.location;

import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.util.geom.CoordinateHolder;
import com.massisframework.massis.util.geom.KVector;

import straightedge.geom.KPoint;

/**
 * Represents a location in the building
 *
 * @author rpax
 *
 */
public class Location implements CoordinateHolder {

    /**
     * 2D point
     */
    protected KPoint center;
    /**
     * Current floor
     */
    protected Floor floor;

    /**
     *
     * @param point the 2D coordinates of this location
     * @param floor the floor
     */
    public Location(KPoint point, Floor floor)
    {

        this.center = point;
        this.floor = floor;
    }

    /**
     * Constructor copy
     *
     * @param other another location
     */
    public Location(Location other)
    {

        this.center = new KPoint(other.center);
        this.floor = other.floor;
    }

    public Location(double x, double y, Floor floor)
    {

        this.center = new KPoint(x, y);
        this.floor = floor;
    }

    public boolean isInSameFloor(final Location other)
    {
        return other.floor == this.floor;
    }

    public double distance2D(final Location other)
    {

        return this.center.distance(other.center);
    }

    public double distance2D(final double x, final double y)
    {
        return this.center.distance(x, y);
    }

    public double distance2D(final KPoint p)
    {
        return this.center.distance(p);
    }

    public boolean isInFloor(Floor f)
    {
        return this.floor == f;
    }

    @Override
    public double getX()
    {
        return this.center.x;
    }

    @Override
    public double getY()
    {
        return this.center.y;
    }

    public void translateTo(double x, double y)
    {
        this.center.x = x;
        this.center.y = y;
    }

    public void translateTo(Location other)
    {
        this.center.x = other.center.x;
        this.center.y = other.center.y;
    }

    protected KPoint getCenter()
    {
        return this.center;
    }

    public Floor getFloor()
    {
        return this.floor;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Location [position=");
        builder.append(this.center);
        builder.append(", floor=");
        builder.append(this.floor.getName());
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.center == null) ? 0 : KVector.hashCode(this.center));
        result = prime * result + ((this.floor == null) ? 0 : this.floor.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof Location))
        {
            return false;
        }
        final Location other = (Location) obj;
        if (this.center == null)
        {
            if (other.center != null)
            {
                return false;
            }
        } else if (this.center.x != other.center.x || this.center.y != other.center.y)
        {
            return false;
        }
        if (this.floor == null)
        {
            if (other.floor != null)
            {
                return false;
            }
        } else if (!this.floor.equals(other.floor))
        {
            return false;
        }
        return true;
    }

//    @Override
    public KPoint getXY()
    {
        return this.center;
    }
}

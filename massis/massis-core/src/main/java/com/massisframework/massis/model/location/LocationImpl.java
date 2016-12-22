package com.massisframework.massis.model.location;

import com.google.inject.Inject;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.components.Location;
import com.massisframework.massis.util.geom.CoordinateHolder;
import com.massisframework.massis.util.geom.KVector;

import straightedge.geom.KPoint;

/**
 * Represents a location in the building
 *
 * @author rpax
 *
 */
public class LocationImpl implements CoordinateHolder, Location {

	/**
	 * 2D point
	 */
	protected KPoint center;
	/**
	 * Current floor
	 */
	protected Floor floor;

	

	/**
	 * Constructor copy
	 *
	 * @param other
	 *            another location
	 */
	@Inject
	private LocationImpl()
	{

		this.center = new KPoint();
		this.floor = null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.massisframework.massis.model.location.Location#isInSameFloor(com.
	 * massisframework.massis.model.location.LocationImpl)
	 */
	@Override
	public boolean isInSameFloor(final Location other)
	{
		return other.getFloor() == this.floor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.location.Location#distance2D(com.
	 * massisframework.massis.model.location.LocationImpl)
	 */
	@Override
	public double distance2D(final Location other)
	{
		return this.distance2D(other.getX(), other.getY());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.massisframework.massis.model.location.Location#distance2D(double,
	 * double)
	 */
	@Override
	public double distance2D(final double x, final double y)
	{
		return this.center.distance(x, y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.location.Location#isInFloor(com.
	 * massisframework.massis.util.Indexable)
	 */
	@Override
	public boolean isInFloor(Floor f)
	{
		return this.floor == f;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.location.Location#getX()
	 */
	@Override
	public double getX()
	{
		return this.center.x;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.location.Location#getY()
	 */
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

	public void translateTo(LocationImpl other)
	{
		this.center.x = other.center.x;
		this.center.y = other.center.y;
	}

	protected KPoint getCenter()
	{
		return this.center;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.location.Location#getFloor()
	 */
	@Override
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
		result = prime * result
				+ ((this.floor == null) ? 0 : this.floor.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.location.Location#equals(java.lang.
	 * Object)
	 */
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
		if (!(obj instanceof CoordinateHolder))
		{
			return false;
		}
		final LocationImpl other = (LocationImpl) obj;
		if (this.center == null)
		{
			if (other.center != null)
			{
				return false;
			}
		} else if (this.center.x != other.center.x
				|| this.center.y != other.center.y)
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

	public void setCoordinates2D(KPoint center)
	{
		this.setCoordinates2D(center.x, center.y);
	}

	public void setCoordinates2D(double x, double y)
	{
		this.center.setCoords(x, y);
	}

	public void setX(double x)
	{
		this.center.x = x;
	}

	public void setY(double y)
	{
		this.center.y = y;
	}

	public void setFloor(Floor floor)
	{
		this.floor = floor;
	}
}

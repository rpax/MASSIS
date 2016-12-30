package com.massisframework.massis.ecs.components;

import java.awt.Rectangle;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.utils.IntBag;
import com.eteks.sweethome3d.model.Level;

public class Floor extends Component {

	/**
	 * @treatAsPrivate
	 */
	public Level level;
	/**
	 * Bounds
	 * 
	 * @treatAsPrivate
	 */
	public int minX = 0, maxX = 1, minY = 0, maxY = 1, xlength = 1,
			ylength = 1;

	public Level getLevel()
	{
		return level;
	}

	public void setLevel(Level level)
	{
		this.level = level;
	}

	public int getMinX()
	{
		return minX;
	}

	public void setMinX(int minX)
	{
		this.minX = minX;
	}

	public int getMaxX()
	{
		return maxX;
	}

	public void setMaxX(int maxX)
	{
		this.maxX = maxX;
	}

	public int getMinY()
	{
		return minY;
	}

	public void setMinY(int minY)
	{
		this.minY = minY;
	}

	public int getMaxY()
	{
		return maxY;
	}

	public void setMaxY(int maxY)
	{
		this.maxY = maxY;
	}

	public int getXlength()
	{
		return xlength;
	}

	public void setXlength(int xlength)
	{
		this.xlength = xlength;
	}

	public int getYlength()
	{
		return ylength;
	}

	public void setYlength(int ylength)
	{
		this.ylength = ylength;
	}

	public void expand(Rectangle bounds)
	{
		minX = (int) Math.min(minX, bounds.getMinX() - 1);
		minY = (int) Math.min(minY, bounds.getMinY() - 1);
		maxX = (int) Math.max(maxX, bounds.getMaxX() + 1);
		maxY = (int) Math.max(maxY, bounds.getMaxY() + 1);
		/*
		 * Prevent zero length bounds
		 */
		if (maxX - minX <= 0)
		{
			minX = 0;
			maxX = 1;
		}
		if (maxY - minY <= 0)
		{
			minY = 0;
			maxY = 1;
		}
		this.xlength = this.maxX - this.minX;
		this.ylength = this.maxY - this.minY;
	}

}

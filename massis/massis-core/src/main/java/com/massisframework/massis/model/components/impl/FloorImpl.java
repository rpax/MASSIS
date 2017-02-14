package com.massisframework.massis.model.components.impl;

import com.massisframework.massis.model.components.Floor;

public class FloorImpl implements Floor {

	private int minX, maxX, minY, maxY;

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.components.impl.IFF#getMinX()
	 */
	@Override
	public int getMinX()
	{
		return minX;
	}

	public void setMinX(int minX)
	{
		this.minX = minX;
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.components.impl.IFF#getMaxX()
	 */
	@Override
	public int getMaxX()
	{
		return maxX;
	}

	public void setMaxX(int maxX)
	{
		this.maxX = maxX;
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.components.impl.IFF#getMinY()
	 */
	@Override
	public int getMinY()
	{
		return minY;
	}

	public void setMinY(int minY)
	{
		this.minY = minY;
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.components.impl.IFF#getMaxY()
	 */
	@Override
	public int getMaxY()
	{
		return maxY;
	}

	public void setMaxY(int maxY)
	{
		this.maxY = maxY;
	}
}

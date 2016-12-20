package com.massisframework.massis.model.components.building.impl;

import com.massisframework.massis.model.components.building.MovementCapabilities;

public class MovementCapabilititesImpl implements MovementCapabilities {

	private boolean isObstacle;
	private boolean canMove;
	
	
	public void setObstacle(boolean isObstacle)
	{
		this.isObstacle = isObstacle;
	}

	public void setCanMove(boolean canMove)
	{
		this.canMove = canMove;
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.components.building.impl.MovementCapabilities#isObstacle()
	 */
	@Override
	public boolean isObstacle()
	{
		return isObstacle;
	}
	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.components.building.impl.MovementCapabilities#canMove()
	 */
	@Override
	public boolean canMove()
	{
		return canMove;
	}
}

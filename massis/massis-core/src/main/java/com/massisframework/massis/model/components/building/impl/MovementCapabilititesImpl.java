package com.massisframework.massis.model.components.building.impl;

import com.massisframework.massis.model.components.building.MovementCapabilities;

public class MovementCapabilititesImpl extends AbstractSimulationComponent implements MovementCapabilities {

	private boolean isObstacle;
	private boolean canMove;
	
	
	public void setObstacle(boolean isObstacle)
	{
		this.isObstacle = isObstacle;
		this.fireChanged();
	}

	public void setCanMove(boolean canMove)
	{
		this.canMove = canMove;
		this.fireChanged();
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

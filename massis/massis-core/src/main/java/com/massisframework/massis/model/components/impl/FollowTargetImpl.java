package com.massisframework.massis.model.components.impl;

import com.massisframework.massis.model.components.FollowTarget;
import com.massisframework.massis.util.geom.CoordinateHolder;
import com.massisframework.massis.util.geom.KVector;

public class FollowTargetImpl implements FollowTarget {

	private KVector target = new KVector();

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.components.impl.FollowTarget#getTarget()
	 */
	@Override
	public CoordinateHolder getTarget()
	{
		return this.target;
	}

	/* (non-Javadoc)
	 * @see com.massisframework.massis.model.components.impl.FollowTarget#setTarget(com.massisframework.massis.util.geom.CoordinateHolder)
	 */
	@Override
	public void setTarget(CoordinateHolder target)
	{
		this.target.set(target.getX(), target.getY());
	}
}

package com.massisframework.massis.model.components.impl;

import com.massisframework.massis.model.components.MovingTo;
import com.massisframework.massis.util.geom.CoordinateHolder;
import com.massisframework.massis.util.geom.KVector;

public class MovingToImpl implements MovingTo {
	private KVector target = new KVector();

	@Override
	public CoordinateHolder getTarget()
	{
		return this.target;
	}

	@Override
	public void setTarget(CoordinateHolder target)
	{
		this.target.set(target.getX(), target.getY());
	}
}

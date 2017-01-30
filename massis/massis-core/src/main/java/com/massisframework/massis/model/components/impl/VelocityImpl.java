package com.massisframework.massis.model.components.impl;

import com.massisframework.massis.model.components.Velocity;
import com.massisframework.massis.util.geom.KVector;

public class VelocityImpl implements Velocity {

	private KVector velocity = new KVector(0, 0);

	public KVector getValue()
	{
		return velocity;
	}

	public void setValue(KVector v)
	{
		this.velocity.set(v.x, v.y);
	}

}

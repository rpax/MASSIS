package com.massisframework.massis.model.components.impl;

import com.massisframework.massis.util.geom.KVector;

public class VelocityImpl {

	private KVector velocity=new KVector(0,0);

	public KVector getVelocity()
	{
		return velocity;
	}

	public void setVelocity(KVector velocity)
	{
		this.velocity = velocity;
	}

}

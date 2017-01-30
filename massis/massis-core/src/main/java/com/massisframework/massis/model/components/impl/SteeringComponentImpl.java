package com.massisframework.massis.model.components.impl;

import com.massisframework.massis.model.components.SteeringComponent;
import com.massisframework.massis.model.managers.movement.steering.SteeringBehavior;
import com.massisframework.massis.util.geom.KVector;

public class SteeringComponentImpl implements SteeringComponent {

	private SteeringBehavior steeringBehavior;
	private KVector acceleration = new KVector(0, 0);
	private float maxForce;
private float maxSpeed;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.components.impl.SteeringComponent#
	 * getSteeringBehavior()
	 */
	@Override
	public SteeringBehavior getSteeringBehavior()
	{
		return this.steeringBehavior;
	}

	public float getMaxForce()
	{
		return maxForce;
	}

	public void setMaxForce(float maxForce)
	{
		this.maxForce = maxForce;
	}

	@Override
	public void setAcceleration(KVector acc)
	{
		this.acceleration.setCoords(acc);
	}

	@Override
	public float getMaxSpeed()
	{
		return this.maxSpeed;
	}
}

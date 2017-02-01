package com.massisframework.massis.model.components.impl;

import com.jme3.math.Vector2f;
import com.massisframework.massis.model.components.Velocity;

public class VelocityImpl implements Velocity {

	private Vector2f velocity = new Vector2f(0, 0);

	public void setValue(Vector2f v)
	{
		this.velocity.set(v.x, v.y);
	}

	@Override
	public Vector2f getValue(Vector2f store)
	{
		return store.set(this.velocity);
	}

	@Override
	public float getX()
	{
		return this.velocity.x;
	}

	@Override
	public float getY()
	{
		return this.velocity.y;
	}

	@Override
	public Velocity setX(float x)
	{
		this.velocity.x = x;
		return this;
	}

	@Override
	public Velocity setY(float y)
	{
		this.velocity.y = y;
		return this;
	}

}

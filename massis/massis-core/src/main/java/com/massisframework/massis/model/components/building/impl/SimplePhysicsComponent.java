package com.massisframework.massis.model.components.building.impl;

import com.massisframework.massis.model.components.building.PhysicsComponent;

public class SimplePhysicsComponent extends AbstractSimulationComponent
		implements PhysicsComponent {

	private double speed;
	private double mass;

	@Override
	public double getSpeed()
	{
		return this.speed;
	}

	@Override
	public void setSpeed(double speed)
	{
		this.speed = speed;
		this.fireChanged();
	}

	@Override
	public void setMass(double mass)
	{
		this.mass = mass;
		this.fireChanged();
	}

	@Override
	public double getMass()
	{
		return this.mass;
	}

}

package com.massisframework.massis.model.components;

import com.massisframework.massis.model.managers.movement.steering.SteeringBehavior;
import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.util.geom.KVector;

public interface SteeringComponent extends SimulationComponent {

	SteeringBehavior getSteeringBehavior();

	public float getMaxForce();

	public void setMaxForce(float maxForce);

	void setAcceleration(KVector steering);

	float getMaxSpeed();
}
package com.massisframework.massis.model.components.building;

import com.massisframework.massis.model.components.SimulationComponent;

public interface PhysicsComponent extends SimulationComponent {

	public double getSpeed();
	
	public void setSpeed(double speed);
	
	public void setMass(double mass);
	
	public double getMass();
}

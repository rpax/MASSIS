package com.massisframework.massis.model.components;

import com.jme3.math.Vector2f;
import com.massisframework.massis.sim.ecs.SimulationComponent;

public interface Velocity extends SimulationComponent {

	Vector2f getValue(Vector2f store);

	void setValue(Vector2f velocity);

	public float getX();

	public float getY();

	public Velocity setX(float x);

	public Velocity setY(float y);

}

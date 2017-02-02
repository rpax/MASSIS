package com.massisframework.massis.model.components;

import com.jme3.math.Vector2f;
import com.massisframework.massis.sim.ecs.SimulationComponent;

public interface TransformComponent extends SimulationComponent {

	Vector2f getPosition(Vector2f store);

	void setLocalTranslation(Vector2f tr);

	float getAngle();

	TransformComponent setAngle(double angle);

	TransformComponent setAngle(float angle);

	TransformComponent rotate(double angle);

	TransformComponent rotate(float angle);

	void ensureUpdated();

	public float getX();

	public float getY();

	public TransformComponent setX(float x);

	public TransformComponent setY(float y);

	public TransformComponent setX(double x);

	public TransformComponent setY(double y);

	public float distance2D(double x, double y);

	public double distanceSquared2D(double otherX, double otherY);
}

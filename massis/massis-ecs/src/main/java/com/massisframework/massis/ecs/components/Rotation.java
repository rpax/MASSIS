package com.massisframework.massis.ecs.components;

public class Rotation extends ModifiableComponent {

	private float angle;

	public void setAngle(float angle)
	{
		this.angle = angle;
		this.fireChanged();
	}

	

}

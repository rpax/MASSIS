package com.massisframework.massis.model.components;

import java.awt.Shape;

import com.massisframework.massis.sim.ecs.SimulationComponent;

public interface ShapeComponent extends SimulationComponent {

	//public Shape getShape();

	public int getNumPoints();

	public double centerX();

	public double centerY();
	
	public void translateTo(double x,double y);
	
	public void translateTo(float x,float y);

	public double getX(int i);

	public double getY(int i);

	public double getRadius();

	public Shape asShape();
}

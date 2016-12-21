package com.massisframework.massis.model.components.building;

import java.awt.Shape;

import com.massisframework.massis.model.components.SimulationComponent;

public interface ShapeComponent extends SimulationComponent {

	public Shape getShape();

	public boolean intersects(Shape other);

	public boolean intersects(ShapeComponent s);
}

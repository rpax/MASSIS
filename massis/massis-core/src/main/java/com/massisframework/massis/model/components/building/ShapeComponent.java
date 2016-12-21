package com.massisframework.massis.model.components.building;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import com.massisframework.massis.model.components.SimulationComponent;

public interface ShapeComponent extends SimulationComponent {

	public Shape getShape();

	public boolean intersects(Shape other);

	public Rectangle2D getBounds();

	public boolean intersectsAABB(Shape other);

	public boolean intersectsAABB(ShapeComponent other);

	public boolean intersects(ShapeComponent s);
}

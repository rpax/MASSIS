package com.massisframework.massis.model.components;

import java.awt.Shape;

import com.massisframework.massis.sim.ecs.SimulationComponent;

import straightedge.geom.KPolygon;

public interface ShapeComponent extends SimulationComponent {

	public Shape getShape();

	public double getRadius();
}

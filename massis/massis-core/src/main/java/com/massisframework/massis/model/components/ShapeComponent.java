package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.SimulationComponent;

import straightedge.geom.KPolygon;

public interface ShapeComponent extends SimulationComponent {

	public KPolygon getShape();
	public void setShape(KPolygon polygon);
	public double getRadius();
}

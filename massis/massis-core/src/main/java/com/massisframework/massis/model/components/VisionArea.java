package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;

import straightedge.geom.KPolygon;

public interface VisionArea extends SimulationComponent {

	public KPolygon getVisionRadioPolygon();
	
	public Iterable<SimulationEntity> getEntitiesInRange();
}

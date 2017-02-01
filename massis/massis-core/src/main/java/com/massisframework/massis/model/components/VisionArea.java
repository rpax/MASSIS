package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.OLDSimulationEntity;
import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;

import straightedge.geom.KPolygon;

public interface VisionArea extends SimulationComponent {

	public KPolygon getVisionRadioPolygon();
	
	public Iterable<OLDSimulationEntity<?>> getEntitiesInRange();
}

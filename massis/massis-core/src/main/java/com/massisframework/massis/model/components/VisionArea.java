package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntity;

import straightedge.geom.KPolygon;

public interface VisionArea extends SimulationComponent {

	// FIXME remove KPolygons dependency
	public KPolygon getVisionRadioPolygon();

	public Iterable<SimulationEntity> getEntitiesInRange();
}

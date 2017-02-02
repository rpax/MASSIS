package com.massisframework.massis.model.components.impl;

import java.util.Collections;

import com.massisframework.massis.model.components.VisionArea;
import com.massisframework.massis.sim.ecs.SimulationEntity;

import straightedge.geom.KPolygon;

public class VisionAreaImpl implements VisionArea {

	@Override
	public KPolygon getVisionRadioPolygon()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<SimulationEntity> getEntitiesInRange()
	{
		return Collections.emptyList();
	}

}

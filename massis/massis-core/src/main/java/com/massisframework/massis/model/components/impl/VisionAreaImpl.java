package com.massisframework.massis.model.components.impl;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.EntityRangeFinder;
import com.massisframework.massis.model.components.VisionArea;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.OLDSimulationEntity;
import com.massisframework.massis.sim.ecs.injection.components.EntityReference;

import straightedge.geom.KPolygon;

public class VisionAreaImpl implements VisionArea {

	private KPolygon visionRadioPolygon;

	@Inject
	SimulationEngine<?> engine;

	@EntityReference
	OLDSimulationEntity<?> entity;

	public VisionAreaImpl()
	{

	}

	public void setVisionRadioPolygon(KPolygon visionRadioPolygon)
	{
		this.visionRadioPolygon = visionRadioPolygon;
	}

	@Override
	public KPolygon getVisionRadioPolygon()
	{
		return this.visionRadioPolygon;
	}

	@Override
	public Iterable<OLDSimulationEntity<?>> getEntitiesInRange()
	{
		return this.entity.get(EntityRangeFinder.class)
				.getEntitiesInRange(this.visionRadioPolygon.getRadius());
	}

}

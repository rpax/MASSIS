package com.massisframework.massis.model.components.impl;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.EntityRangeFinder;
import com.massisframework.massis.model.components.VisionArea;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.injection.components.EntityReference;

import straightedge.geom.KPolygon;
import straightedge.geom.vision.VisionFinder;

public class VisionAreaImpl implements VisionArea {

	private KPolygon visionRadioPolygon;

	@Inject
	SimulationEngine engine;

	@EntityReference
	SimulationEntity entity;

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
	public Iterable<SimulationEntity> getEntitiesInRange()
	{
		return this.entity.getComponent(EntityRangeFinder.class)
				.getEntitiesInRange(this.visionRadioPolygon.getRadius());
	}

}

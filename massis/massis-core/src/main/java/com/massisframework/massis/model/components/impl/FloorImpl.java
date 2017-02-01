package com.massisframework.massis.model.components.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.Floor;
import com.massisframework.massis.model.components.FloorReference;
import com.massisframework.massis.model.components.Position2D;
import com.massisframework.massis.sim.FilterParams;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.OLDSimulationEntity;
import com.massisframework.massis.sim.ecs.injection.components.EntityReference;

public class FloorImpl implements Floor {

	@Inject
	private SimulationEngine<?> engine;
	@FilterParams(all = { FloorReference.class })
	private ComponentFilter<?> referenceFilter;

	@EntityReference
	OLDSimulationEntity<?> entity;

	private List<OLDSimulationEntity<?>> entities = new ArrayList<>();
	/*
	 * Bounds
	 */
	public int minX, maxX, minY, maxY, xlength, ylength;

	@Override
	public Iterable<OLDSimulationEntity<?>> getEntitiesIn()
	{
		return engine.getEntitiesFor(referenceFilter, entities).stream()
				.filter(e -> e.get(FloorReference.class)
						.getFloorId() == this.entity.getId())::iterator;
	}

	public int getMinX()
	{
		return StreamSupport.stream(getEntitiesIn().spliterator(), false)
				.map(e -> e.get(Position2D.class)).filter(p -> p != null)
				.mapToInt(p -> (int) p.getX()).min().orElseGet(() -> 0);
	}

	public void setMinX(int minX)
	{
		this.minX = minX;
	}

	public int getMaxX()
	{
		return StreamSupport.stream(getEntitiesIn().spliterator(), false)
				.map(e -> e.get(Position2D.class)).filter(p -> p != null)
				.mapToInt(p -> (int) p.getX()).max().orElseGet(() -> 0);
	}

	public void setMaxX(int maxX)
	{
		this.maxX = maxX;
	}

	public int getMinY()
	{
		return StreamSupport.stream(getEntitiesIn().spliterator(), false)
				.map(e -> e.get(Position2D.class)).filter(p -> p != null)
				.mapToInt(p -> (int) p.getY()).min().orElseGet(() -> 0);
	}

	public void setMinY(int minY)
	{
		this.minY = minY;
	}

	public int getMaxY()
	{
		return StreamSupport.stream(getEntitiesIn().spliterator(), false)
				.map(e -> e.get(Position2D.class)).filter(p -> p != null)
				.mapToInt(p -> (int) p.getY()).max().orElseGet(() -> 0);
	}

	public void setMaxY(int maxY)
	{
		this.maxY = maxY;
	}

	public int getXlength()
	{
		return xlength;
	}

	public void setXlength(int xlength)
	{
		this.xlength = xlength;
	}

	public int getYlength()
	{
		return getMaxY() - getMinY();
	}

	public void setYlength(int ylength)
	{
		this.ylength = ylength;
	}

}

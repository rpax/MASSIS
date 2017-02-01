package com.massisframework.massis.model.components.impl;

import java.util.stream.StreamSupport;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.Floor;
import com.massisframework.massis.model.components.FloorReference;
import com.massisframework.massis.model.components.TransformComponent;
import com.massisframework.massis.sim.ecs.injection.components.EntityReference;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntity;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntityData;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntitySet;

public class FloorImpl implements Floor {

	@Inject
	private SimulationEntityData entityData;

	private SimulationEntitySet entities;

	@EntityReference
	private SimulationEntity entity;

	@Inject
	public FloorImpl()
	{
		this.entities = entityData.createEntitySet(FloorReference.class);
	}

	/*
	 * Bounds
	 */
	public int minX, maxX, minY, maxY, xlength, ylength;

	@Override
	public Iterable<SimulationEntity> getEntitiesIn()
	{
		this.entities.applyChanges();
		return StreamSupport.stream(this.entities.spliterator(), false)
				.filter(e -> e.getC(FloorReference.class)
						.getFloorId() == this.entity.getId().getId())::iterator;
	}

	public int getMinX()
	{
		return StreamSupport.stream(getEntitiesIn().spliterator(), false)
				.map(e -> e.getC(TransformComponent.class))
				.mapToInt(p -> (int) p.getX()).min().orElseGet(() -> 0);
	}
	
	public void setMinX(int minX)
	{
		this.minX = minX;
	}

	public int getMaxX()
	{
		return StreamSupport.stream(getEntitiesIn().spliterator(), false)
				.map(e -> e.getC(TransformComponent.class))
				.mapToInt(p -> (int) p.getX()).max().orElseGet(() -> 0);
	}

	public void setMaxX(int maxX)
	{
		this.maxX = maxX;
	}

	public int getMinY()
	{
		return StreamSupport.stream(getEntitiesIn().spliterator(), false)
				.map(e -> e.getC(TransformComponent.class))
				.mapToInt(p -> (int) p.getY()).min().orElseGet(() -> 0);
	}

	public void setMinY(int minY)
	{
		this.minY = minY;
	}

	public int getMaxY()
	{
		return StreamSupport.stream(getEntitiesIn().spliterator(), false)
				.map(e -> e.getC(TransformComponent.class))
				.mapToInt(p -> (int) p.getX()).max().orElseGet(() -> 0);
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

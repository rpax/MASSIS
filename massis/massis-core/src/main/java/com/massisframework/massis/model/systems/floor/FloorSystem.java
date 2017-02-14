package com.massisframework.massis.model.systems.floor;

import static com.massisframework.massis.sim.ecs.CollectionsFactory.*;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.massisframework.massis.model.components.FloorReference;
import com.massisframework.massis.model.components.TransformComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationEntityData;
import com.massisframework.massis.sim.ecs.SimulationEntitySet;
import com.massisframework.massis.sim.ecs.SimulationSystem;

public class FloorSystem implements SimulationSystem {

	private SimulationEntityData ed;
	private SimulationEntitySet floorReferences;
	private Map<Long, List<Long>> floorEntities;

	@Inject
	public FloorSystem(SimulationEntityData ed)
	{
		this.ed = ed;
		this.floorEntities = newMapList(Long.class, Long.class);
	}

	@Override
	public void initialize()
	{
		this.floorReferences = this.ed.createEntitySet(
				FloorReference.class,
				TransformComponent.class);
	}

	@Override
	public void update(float deltaTime)
	{
		if (this.floorReferences.applyChanges())
		{
			this.recomputeBounds();
		}
	}

	private void recomputeBounds()
	{

		for (SimulationEntity e : floorReferences)
		{
			long fId = e.get(FloorReference.class).getFloorId();
			List<Long> entitiesInFloor = this.floorEntities.get(fId);
			if (entitiesInFloor == null)
			{
				entitiesInFloor = newList(Long.class);
				this.floorEntities.put(fId, entitiesInFloor);
			}
			entitiesInFloor.add(e.id());
		}
	}

}

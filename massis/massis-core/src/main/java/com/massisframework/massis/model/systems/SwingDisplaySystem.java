package com.massisframework.massis.model.systems;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.massisframework.gui.DrawableTabbedFrame;
import com.massisframework.gui.EngineDrawableZone;
import com.massisframework.massis.displays.floormap.layers.LayerComponent;
import com.massisframework.massis.model.components.Floor;
import com.massisframework.massis.sim.FilterParams;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.ComponentFilterBuilder;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationSystem;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

public class SwingDisplaySystem implements SimulationSystem {

	// TODO
	@FilterParams(all = LayerComponent.class)
	private ComponentFilter layersFilter;
	@FilterParams(all = Floor.class)
	private ComponentFilter floorFilter;

	private List<SimulationEntity> entities;
	private Set<Integer> added;
	private Map<Integer, EngineDrawableZone> floorMap;
	private DrawableTabbedFrame frame;
	@Inject
	private SimulationEngine engine;

	

	@Inject
	private Provider<ComponentFilterBuilder> cFBuilder;

	@Override
	public void initialize()
	{
		this.added = new IntOpenHashSet();
		this.frame = new DrawableTabbedFrame();
		this.frame.pack();
		this.frame.setVisible(true);
		this.floorMap = new Int2ObjectOpenHashMap<>();
	}

	@Override
	public void update(float deltaTime)
	{
		for (SimulationEntity e : this.engine.getEntitiesFor(layersFilter,
				entities))
		{
			if (!this.added.contains(e.getId()))
			{
				this.added.add(e.getId());
				this.frame.addDrawableLayer(
						e.get(LayerComponent.class).getLayer());
			}
		}
		for (SimulationEntity e : this.engine.getEntitiesFor(floorFilter,
				entities))
		{
			if (!this.floorMap.containsKey(e.getId()))
			{
				EngineDrawableZone dz = new EngineDrawableZone(e.getId(),
						cFBuilder, engine);
				this.floorMap.put(e.getId(), dz);
				this.frame.addDrawableZone(dz);
			}
		}
		this.frame.refresh();
		//
	}

}

package com.massisframework.massis.model.systems.rendering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.massisframework.gui.EngineDrawableZone;
import com.massisframework.massis.displays.floormap.layers.LayerComponent;
import com.massisframework.massis.javafx.util.ApplicationLauncher;
import com.massisframework.massis.model.components.Floor;
import com.massisframework.massis.model.components.RenderComponent;
import com.massisframework.massis.sim.FilterParams;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationSystem;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class JFXDisplaySystem implements SimulationSystem {

	// TODO
	@FilterParams(all = LayerComponent.class)
	private ComponentFilter<?> layersFilter;
	@FilterParams(all = Floor.class)
	private ComponentFilter<?> floorFilter;
	@FilterParams(all = RenderComponent.class)
	private ComponentFilter<?> renderFilter;
	private Simulation2DWindow window;
	private List<SimulationEntity<?>> entities;
	private Set<Integer> added;
	private Map<Integer, EngineDrawableZone> floorMap;
	@Inject
	private SimulationEngine<?> engine;

	@Override
	public void initialize()
	{
		this.entities=new ArrayList<>();
		this.added = new IntOpenHashSet();
		this.floorMap = new Int2ObjectOpenHashMap<>();
		ApplicationLauncher.launchWrappedApplication((stage, app) -> {
			try
			{
				FXMLLoader loader = new FXMLLoader(getClass()
						.getResource("Simulation2DWindow.fxml"));
				Parent root = loader.load();
				this.window = loader.getController();
				stage.setScene(new Scene(root, 800, 600));
				stage.show();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	@Override
	public void update(float deltaTime)
	{
		this.engine.getEntitiesFor(renderFilter, this.entities);
		GraphicsContext g2c = this.window.getCanvas().getGraphicsContext2D();
		for (SimulationEntity<?> se : this.entities)
		{
			se.get(RenderComponent.class).getRenderer().render(se, g2c);
		}
		// for (SimulationEntity e : this.engine.getEntitiesFor(layersFilter,
		// entities))
		// {
		// if (!this.added.contains(e.getId()))
		// {
		// this.added.add(e.getId());
		// this.frame.addDrawableLayer(
		// e.get(LayerComponent.class).getLayer());
		// }
		// }
		// for (SimulationEntity e : this.engine.getEntitiesFor(floorFilter,
		// entities))
		// {
		// if (!this.floorMap.containsKey(e.getId()))
		// {
		// EngineDrawableZone dz = new EngineDrawableZone(e.getId(),
		// cFBuilder, engine);
		// this.floorMap.put(e.getId(), dz);
		// this.frame.addDrawableZone(dz);
		// }
		// }
		// this.frame.refresh();
		//
	}

}

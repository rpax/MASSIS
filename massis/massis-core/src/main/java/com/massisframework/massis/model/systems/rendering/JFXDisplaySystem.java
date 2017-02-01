package com.massisframework.massis.model.systems.rendering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.massisframework.massis.javafx.util.ApplicationLauncher;
import com.massisframework.massis.model.components.Floor;
import com.massisframework.massis.model.components.RenderComponent;
import com.massisframework.massis.sim.FilterParams;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.OLDSimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationSystem;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class JFXDisplaySystem implements SimulationSystem {

	// TODO
	@FilterParams(all = Floor.class)
	private ComponentFilter<?> floorFilter;
	@FilterParams(all = RenderComponent.class)
	private ComponentFilter<?> renderFilter;
	private Simulation2DWindow window;
	private List<OLDSimulationEntity<?>> entities;
	@Inject
	private SimulationEngine<?> engine;

	@Override
	public void initialize()
	{

		this.entities = new ArrayList<>();
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
		window.setEntities(this.entities);
	}

}

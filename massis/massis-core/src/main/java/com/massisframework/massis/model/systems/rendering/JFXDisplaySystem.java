package com.massisframework.massis.model.systems.rendering;

import java.io.IOException;

import com.google.inject.Inject;
import com.massisframework.massis.javafx.util.ApplicationLauncher;
import com.massisframework.massis.model.components.RenderComponent;
import com.massisframework.massis.sim.ecs.SimulationSystem;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntityData;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntitySet;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class JFXDisplaySystem implements SimulationSystem {

	// TODO
	private Simulation2DWindow window;

	@Inject
	private SimulationEntityData ed;

	private SimulationEntitySet entities;

	@Override
	public void initialize()
	{
		this.entities = this.ed.createEntitySet(RenderComponent.class);

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
		this.entities.applyChanges();
		
		window.setEntities(this.entities);
	}

}

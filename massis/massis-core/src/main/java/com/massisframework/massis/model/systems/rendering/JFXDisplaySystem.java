package com.massisframework.massis.model.systems.rendering;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.google.inject.Inject;
import com.massisframework.massis.javafx.util.ApplicationLauncher;
import com.massisframework.massis.model.components.RenderComponent;
import com.massisframework.massis.model.components.ShapeComponent;
import com.massisframework.massis.model.components.TransformComponent;
import com.massisframework.massis.sim.ecs.SimulationEntityData;
import com.massisframework.massis.sim.ecs.SimulationEntitySet;
import com.massisframework.massis.sim.ecs.SimulationSystem;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class JFXDisplaySystem implements SimulationSystem {

	// TODO
	private CompletableFuture<Simulation2DWindow> window = new CompletableFuture<>();;

	@Inject
	private SimulationEntityData ed;

	private SimulationEntitySet entities;

	@Override
	public void initialize()
	{
		this.entities = this.ed.createEntitySet(
				RenderComponent.class,
				TransformComponent.class,
				ShapeComponent.class);

		ApplicationLauncher.launchWrappedApplication((stage, app) -> {
			try
			{
				FXMLLoader loader = new FXMLLoader(getClass()
						.getResource("Simulation2DWindow.fxml"));
				Parent root = loader.load();
				Simulation2DWindow controller = loader.getController();
				this.window.complete(controller);
				stage.setScene(new Scene(root, 800, 600));
				stage.show();
			} catch (IOException e)
			{
				this.window.completeExceptionally(e);
			}
		});
	}

	@Override
	public void update(float deltaTime)
	{
		this.entities.applyChanges();
		try
		{
			window.get().setEntities(this.entities);
		} catch (InterruptedException | ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

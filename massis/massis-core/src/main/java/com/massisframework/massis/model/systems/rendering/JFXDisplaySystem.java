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
import com.massisframework.massis.model.components.Position2D;
import com.massisframework.massis.model.components.RenderComponent;
import com.massisframework.massis.sim.FilterParams;
import com.massisframework.massis.sim.ecs.ComponentFilter;
import com.massisframework.massis.sim.ecs.SimulationEngine;
import com.massisframework.massis.sim.ecs.SimulationEntity;
import com.massisframework.massis.sim.ecs.SimulationSystem;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;

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
	private AnimationTimer timer;

	@Override
	public void initialize()
	{
		this.timer = createAnimationTimer();
		this.entities = new ArrayList<>();
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
				timer.start();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	private AnimationTimer createAnimationTimer()
	{
		return new AnimationTimer() {

			@Override
			public void handle(long now)
			{

				Canvas canvas = window.getCanvas();
				GraphicsContext g2c = canvas.getGraphicsContext2D();

				tr.setToIdentity();
				g2c.setTransform(tr);

				window.refresh();

				canvas.getGraphicsContext2D().clearRect(0, 0,
						canvas.getWidth(),
						canvas.getHeight());

				double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE,
						minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
				synchronized (entities)
				{
					for (SimulationEntity<?> se : entities)
					{
						Position2D position = se.get(Position2D.class);
						if (position != null)
						{
							minX = Math.min(minX, position.getX());
							minY = Math.min(minY, position.getY());

							maxX = Math.max(maxX, position.getX());
							maxY = Math.max(maxY, position.getY());
						}
					}

					double scale = Math.min(canvas.getWidth() / (maxX - minX),
							canvas.getHeight() / (maxY - minY)) * 0.9;
					double translateX = -minX;
					double translateY = -minY;

					tr.appendScale(scale, scale);
					tr.appendTranslation(translateX, translateY);
					g2c.transform(tr);
					for (SimulationEntity<?> se : entities)
					{
						se.get(RenderComponent.class).getRenderer().render(se,
								g2c);
					}
				}
			}
		};
	}

	private Affine tr = new Affine();

	@Override
	public void update(float deltaTime)
	{
		synchronized (entities)
		{
			this.engine.getEntitiesFor(renderFilter, this.entities);
		}

	}

}

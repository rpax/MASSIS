package com.massisframework.massis.ecs.system.graphics;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.massisframework.massis.ecs.components.BuildingLocation;
import com.massisframework.massis.ecs.components.PolygonComponent;
import com.massisframework.massis.javafx.canvas2d.CanvasDrawable;
import com.massisframework.massis.javafx.canvas2d.CanvasTabbedPane;
import com.massisframework.massis.javafx.util.ApplicationLauncher;

import javafx.application.Platform;
import javafx.scene.Scene;

public class Graphics2DSystem extends BaseEntitySystem {

	// private Map<Integer, IntBag> entitiesToDraw;
	// How to
	public Graphics2DSystem()
	{
		super(Aspect.all(BuildingLocation.class, PolygonComponent.class));
	}

	@Override
	protected void initialize()
	{
		super.initialize();
		// this.entitiesToDraw = new HashMap<>();
		ApplicationLauncher.launchWrappedApplication((stage, app) -> {
			Scene scene = new Scene(new CanvasTabbedPane());
			stage.setScene(scene);
			stage.show();
		});
	}

	@Override
	protected void processSystem()
	{
		// getFrame().refresh();
		// this.entitiesToDraw.values().forEach(IntBag::clear);
		// iterate(subscription, world).forEach(entity -> {
		// int fid = entity.getComponent(BuildingLocation.class).getFloorId();
		// IntBag entities = entitiesToDraw.get(fid);
		// if (entities == null)
		// {
		// entities = new IntBag();
		// entitiesToDraw.put(fid, entities);
		// }
		// entities.add(entity.getId());
		// });
	}

	public void addDrawableZone(CanvasDrawable<?> dz)
	{
		Platform.runLater(() -> {

		});
	}

	

}

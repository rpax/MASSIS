package com.massisframework.massis.ecs.system.graphics.jfx;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.IteratingSystem;
import com.massisframework.massis.ecs.components.Floor;
import com.massisframework.massis.ecs.components.NameComponent;
import com.massisframework.massis.ecs.system.location.LocationSystem;
import com.massisframework.massis.ecs.util.SimulationObjects;

import static com.massisframework.massis.ecs.util.EntitiesCollections.*;
import com.massisframework.massis.javafx.canvas2d.CanvasTabbedPane;

import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

public class FloorFXSystem extends IteratingSystem {

	private CanvasTabbedPane canvasTabbedPane;

	public FloorFXSystem()
	{
		super(Aspect.all(Floor.class, NameComponent.class));
	}

	@Override
	protected void process(int entityId)
	{

	}

	@Override
	protected void initialize()
	{
		super.initialize();
		world.getSystem(JavaFXSystem.class).runOnJFXThread(() -> {
			Stage mainStage = new Stage();
			this.canvasTabbedPane = new CanvasTabbedPane();
			Scene mainScene = new Scene(this.canvasTabbedPane);
			mainStage.setScene(mainScene);
			mainStage.show();
		});
	}

	@Override
	protected void inserted(int entityId)
	{
		super.inserted(entityId);
		String name = world.getEntity(entityId)
				.getComponent(NameComponent.class).get();
		world.getSystem(JavaFXSystem.class).runOnJFXThread(() -> {
			this.canvasTabbedPane.addTab(name, (gc) -> {
				drawItemsInFloor(entityId, gc);
			});
		});
	}

	private void drawItemsInFloor(int floorId, GraphicsContext gc)
	{
		LocationSystem locationSystem = world.getSystem(LocationSystem.class);
		// Get linked items from floor to doors etc.
		for (int entityId : iterate(locationSystem.getItemsInFloor(floorId)))
		{
			Entity entity = world.getEntity(entityId);
			
			if (SimulationObjects.isWall(entity))
			{
				//
			}

		}

	}

	@Override
	protected void dispose()
	{
		super.dispose();
	}

}

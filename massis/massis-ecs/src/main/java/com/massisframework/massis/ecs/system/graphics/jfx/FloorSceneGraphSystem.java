package com.massisframework.massis.ecs.system.graphics.jfx;

import java.util.HashMap;
import java.util.Map;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.link.EntityLinkManager;
import com.artemis.link.LinkAdapter;
import com.artemis.link.LinkListener;
import com.artemis.systems.IteratingSystem;
import com.massisframework.massis.ecs.components.BuildingLocation;
import com.massisframework.massis.ecs.components.Floor;
import com.massisframework.massis.ecs.components.NameComponent;
import com.massisframework.massis.ecs.components.g2d.shape.JFXShapeComponent;
import com.massisframework.massis.javafx.canvas2d.tabbedpane.CanvasTabbedPane;
import com.massisframework.massis.javafx.canvas2d.tabbedpane.JFXSceneGraph;

import javafx.scene.Scene;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class FloorSceneGraphSystem extends IteratingSystem {

	private CanvasTabbedPane canvasTabbedPane;
	private Map<Integer, JFXSceneGraph> floorTabs;
	private LinkListener jfxShapeLinkListener;

	public FloorSceneGraphSystem()
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
		this.floorTabs = new HashMap<>();
		world.getSystem(JavaFXSystem.class).runOnJFXThread(() -> {
			Stage mainStage = new Stage();
			this.canvasTabbedPane = new CanvasTabbedPane();
			Scene mainScene = new Scene(this.canvasTabbedPane);
			mainStage.setScene(mainScene);
			mainStage.show();
		});
		this.jfxShapeLinkListener = createJFXShapeListener();
		this.world.getSystem(EntityLinkManager.class)
				.register(JFXShapeComponent.class, this.jfxShapeLinkListener);
	}

	private LinkListener createJFXShapeListener()
	{
		return new LinkAdapter() {
			@Override
			public void onLinkEstablished(
					int sourceId,
					int targetId)
			{
				Entity simulationEntity = world.getEntity(targetId);
				BuildingLocation loc = simulationEntity
						.getComponent(BuildingLocation.class);
				int floorId = loc.getFloorId();
				Entity shapeEntity = world.getEntity(sourceId);
				JFXShapeComponent shapeNode = shapeEntity
						.getComponent(JFXShapeComponent.class);
				Shape shape = shapeNode.getShape();
				world.getSystem(JavaFXSystem.class).runOnJFXThread(() -> {
					JFXSceneGraph floorTab = floorTabs.get(floorId);
					floorTab.addChild(shape);
				});
			}
		};
	}


	@Override
	protected void inserted(int entityId)
	{
		super.inserted(entityId);
		String name = world.getEntity(entityId)
				.getComponent(NameComponent.class).get();
		world.getSystem(JavaFXSystem.class).runOnJFXThread(() -> {
			JFXSceneGraph sg = this.canvasTabbedPane.addTab(name);
			this.floorTabs.put(entityId, sg);
		});
	}

	@Override
	protected void removed(int entityId)
	{
		super.removed(entityId);
		world.getSystem(JavaFXSystem.class).runOnJFXThread(() -> {
			JFXSceneGraph sg = this.floorTabs.remove(entityId);
			this.canvasTabbedPane.removeTab(sg);
		});

	}

	@Override
	protected void dispose()
	{
		super.dispose();
	}

}

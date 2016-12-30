package com.massisframework.massis.ecs.system.graphics.jfx;

import java.util.HashMap;
import java.util.Map;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.massisframework.massis.ecs.components.Floor;
import com.massisframework.massis.ecs.components.NameComponent;
import com.massisframework.massis.javafx.canvas2d.CanvasTabbedPane;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class FloorFXSystem extends IteratingSystem {

	private CanvasTabbedPane canvasTabbedPane;
	private Map<Integer, Group> floorTabs;

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
		this.floorTabs = new HashMap<>();
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
			Group tab = this.canvasTabbedPane.addTab(name);
			
			AnchorPane content = new AnchorPane();
			Rectangle r = new Rectangle(10, 10);
			r.setFill(Color.BLUE);
			r.setTranslateX(10);
			r.setTranslateY(10);
			content.getChildren().add(r);
			tab.getChildren().add(content);
			this.floorTabs.put(entityId, tab);
		});
	}

	@Override
	protected void removed(int entityId)
	{
		super.removed(entityId);
		world.getSystem(JavaFXSystem.class).runOnJFXThread(() -> {
			Group tab = this.floorTabs.remove(entityId);
			this.canvasTabbedPane.removeTab(tab);
		});

	}

	@Override
	protected void dispose()
	{
		super.dispose();
	}

}

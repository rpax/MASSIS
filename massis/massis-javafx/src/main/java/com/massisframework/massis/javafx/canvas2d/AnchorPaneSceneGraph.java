package com.massisframework.massis.javafx.canvas2d;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;

public class AnchorPaneSceneGraph extends AnchorPane implements JFXSceneGraph {

	final double SCALE_DELTA = 1.1;
	private Group innerGroup;
	private Group sceneGroup;

	private DragContext dragContext;

	public AnchorPaneSceneGraph()
	{
		this.dragContext = new DragContext();
		this.innerGroup = new Group();
		this.sceneGroup = new Group();
		this.innerGroup.getChildren().add(sceneGroup);
		this.getChildren().add(this.innerGroup);
		this.setOnMousePressed(this::onMousePressed);
		this.setOnMouseDragged(this::onMouseDragged);
		this.setOnScroll(this::onScroll);
		this.setStyle("-fx-background-color: #000000;");
	}

	private static final class DragContext {
		public double mouseAnchorX;
		public double mouseAnchorY;
		public double initialTranslateX;
		public double initialTranslateY;
	}

	private void onMousePressed(MouseEvent evt)
	{
		// remember initial mouse cursor coordinates
		// and node position
		dragContext.mouseAnchorX = evt.getX();
		dragContext.mouseAnchorY = evt.getY();
		dragContext.initialTranslateX = sceneGroup
				.getTranslateX();
		dragContext.initialTranslateY = sceneGroup
				.getTranslateY();
	}

	private void onMouseDragged(MouseEvent evt)
	{
		sceneGroup.setTranslateX(
				dragContext.initialTranslateX
						+ evt.getX()
						- dragContext.mouseAnchorX);
		sceneGroup.setTranslateY(
				dragContext.initialTranslateY
						+ evt.getY()
						- dragContext.mouseAnchorY);
	}

	private void onScroll(ScrollEvent evt)
	{
		if (evt.getDeltaY() == 0)
		{
			return;
		}

		double scaleFactor = (evt.getDeltaY() > 0)
				? SCALE_DELTA
				: 1 / SCALE_DELTA;
		sceneGroup.setScaleX(sceneGroup.getScaleX() * scaleFactor);
		sceneGroup.setScaleY(sceneGroup.getScaleY() * scaleFactor);
	}

	public void addChild(Node node)
	{
		this.sceneGroup.getChildren().add(node);
	}

	public void removeChild(Node node)
	{
		this.sceneGroup.getChildren().remove(node);
	}

}

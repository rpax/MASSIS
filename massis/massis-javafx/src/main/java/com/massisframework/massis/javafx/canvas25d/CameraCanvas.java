package com.massisframework.massis.javafx.canvas25d;

import com.massisframework.massis.javafx.JFXController;

import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;

public class CameraCanvas extends AnchorPane implements JFXController {
	private static class DragData {
		double initialX = 0;
		double initialY = 0;
		boolean dragging = false;
	}

	@FXML
	private Camera camera;
	@FXML
	private SubScene scene3D;
	private float MOVE_SPEED = 5;
	private Group root3D;
	private DragData dragData;
	private CameraCanvas root;

	public CameraCanvas()
	{
		inject();
	}

	@FXML
	public void initialize()
	{
		this.root = this;
		this.root3D = new Group();
		this.dragData = new DragData();
		scene3D.setCamera(camera);
		scene3D.setManaged(false);
		scene3D.widthProperty().bind(this.root.widthProperty());
		scene3D.heightProperty().bind(this.root.heightProperty());
		root3D.setRotationAxis(new Point3D(1, 0, 0));
		this.root3D.setRotate(90);
		final PhongMaterial redMaterial = new PhongMaterial();
		redMaterial.setDiffuseColor(Color.DARKRED);
		redMaterial.setSpecularColor(Color.RED);
		for (int i = 0; i < 10; i++)
		{
			Cylinder c = new Cylinder(10, 1);
			c.setTranslateX(i * 20);
			c.setTranslateZ(0);
			c.setTranslateY(0);
			c.setMaterial(redMaterial);
			this.root3D.getChildren().add(c);
		}

		this.scene3D.setRoot(root3D);
		this.root.setOnMouseDragged(this::onMouseDragged);
		// this.getRoot().setOnMouseReleased(this::onDragReleased);
		this.root.setOnScroll(this::onScroll);
	}

	public void onMouseReleased(MouseEvent evt)
	{
		dragData.dragging = false;
	}

	public void onMouseDragged(MouseEvent evt)
	{
		if (!dragData.dragging)
		{
			dragData.dragging = true;
		} else
		{
			double dx = -(evt.getScreenX() - dragData.initialX);
			double dy = -(evt.getScreenY() - dragData.initialY);
			moveCam(dx, dy);
		}
		dragData.initialX = evt.getScreenX();
		dragData.initialY = evt.getScreenY();
	}

	public void onScroll(ScrollEvent evt)
	{
		zoomCam(evt.getDeltaY());
	}

	private void zoomCam(double deltaY)
	{
		this.camera.setTranslateZ(this.camera.getTranslateZ() + deltaY);
	}

	private void setCamX(final double x)
	{
		camera.setTranslateX(x);
	}

	private void setCamY(final double y)
	{
		camera.setTranslateY(y);
	}

	private double getCamX()
	{
		return this.camera.getTranslateX();
	}

	private double getCamY()
	{
		return this.camera.getTranslateY();
	}

	private void moveCam(final double dx, final double dy)
	{
		setCamX(getCamX() + dx);
		setCamY(getCamY() + dy);
	}

	public void onKeyPressed(KeyEvent evt)
	{
		System.out.println("Key pressed!");
		switch (evt.getCode())
		{
		case W:
			moveCam(0, -MOVE_SPEED);
			break;
		case S:
			moveCam(0, +MOVE_SPEED);
			break;
		case A:
			moveCam(-MOVE_SPEED, 0);
			break;
		case D:
			moveCam(+MOVE_SPEED, 0);
			break;
		default:
			break;
		}

	}

	public Group getRoot3D()
	{
		return root3D;
	}

	public void attachChild(Shape3D child)
	{
		this.root3D.getChildren().add(child);
	}

	public void detachChild(Shape3D child)
	{
		this.root3D.getChildren().remove(child);
	}
}

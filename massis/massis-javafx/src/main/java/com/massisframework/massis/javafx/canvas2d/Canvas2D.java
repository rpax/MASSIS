package com.massisframework.massis.javafx.canvas2d;

import com.massisframework.massis.javafx.JFXController;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.transform.Affine;

public class Canvas2D extends AnchorPane implements JFXController {

	private Canvas canvas;
	private double pressedX, pressedY;
	private Affine transform;

	public Canvas2D()
	{
		inject();
	}

	@FXML
	public void initialize()
	{
		this.transform = new Affine();
		this.canvas = new Canvas();

		AnchorPane.setBottomAnchor(canvas, 0D);
		AnchorPane.setTopAnchor(canvas, 0D);
		AnchorPane.setLeftAnchor(canvas, 0D);
		AnchorPane.setRightAnchor(canvas, 0D);
		canvas.setWidth(100);
		canvas.setHeight(100);
		this.getChildren().add(this.canvas);

		this.widthProperty().addListener((obs, o, n) -> {
			canvas.setWidth(n.doubleValue());
			drawShapes(canvas.getGraphicsContext2D());
		});
		this.heightProperty().addListener((obs, o, n) -> {
			canvas.setHeight(n.doubleValue());
			drawShapes(canvas.getGraphicsContext2D());
		});
		
		// canvas.widthProperty().bind(this.widthProperty());
		// canvas.heightProperty().bind(this.heightProperty());
		drawShapes(canvas.getGraphicsContext2D());
		this.enablePan();
		this.enableZoom();

	}

	private void enableZoom()
	{
		canvas.setOnScroll(evt -> {
			double dy = evt.getDeltaY();
			double inc = dy > 0 ? 1.1 : 0.9;
			this.transform.appendScale(inc, inc);
			// this.canvas.getGraphicsContext2D().setTransform(transform);
			this.drawShapes(canvas.getGraphicsContext2D());
			// evt.consume();
		});
		canvas.onKeyPressedProperty().addListener(evt -> {
			System.out.println("PRESSED");
		});
		canvas.setOnKeyReleased(evt -> {
			double dy = 1;
			System.out.println(evt);
			switch (evt.getCode())
			{
			case Z:
				dy = 1;
				break;
			case X:
				dy = -1;
				break;
			}
			double inc = dy > 0 ? 1.1 : 0.9;
			this.transform.appendScale(inc, inc);
			this.drawShapes(canvas.getGraphicsContext2D());
			evt.consume();
		});
	}

	private void enablePan()
	{
		canvas.setOnMousePressed(event -> {
			pressedX = event.getX();
			pressedY = event.getY();
		});
		canvas.setOnMouseDragged(event -> {
			this.transform.setTx(
					canvas.getTranslateX() + event.getX() - pressedX);
			this.transform.setTy(
					canvas.getTranslateY() + event.getY() - pressedY);

			this.drawShapes(canvas.getGraphicsContext2D());
			event.consume();
		});

	}

	static Affine IDENTITY = new Affine();

	private void drawShapes(GraphicsContext gc)
	{

		gc.setTransform(IDENTITY);
		gc.setFill(Color.WHITE);
		gc.setStroke(Color.WHITE);
		gc.fillRect(0, 0, gc.getCanvas().getWidth(),
				gc.getCanvas().getHeight());
		gc.setTransform(transform);
		gc.setFill(Color.GREEN);
		gc.setStroke(Color.BLUE);
		gc.setLineWidth(5);
		gc.strokeLine(40, 10, 10, 40);
		gc.fillOval(10, 60, 30, 30);
		gc.strokeOval(60, 60, 30, 30);
		gc.fillRoundRect(110, 60, 30, 30, 10, 10);
		gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
		gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
		gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
		gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
		gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
		gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
		gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
		gc.fillPolygon(new double[] { 10, 40, 10, 40 },
				new double[] { 210, 210, 240, 240 }, 4);
		gc.strokePolygon(new double[] { 60, 90, 60, 90 },
				new double[] { 210, 210, 240, 240 }, 4);
		gc.strokePolyline(new double[] { 110, 140, 110, 140 },
				new double[] { 210, 210, 240, 240 }, 4);
		gc.setFill(Color.RED);
		gc.setStroke(Color.RED);
		for (int i = 0; i < 100; i++)
		{
			gc.fillRect(i * 10, i * 10, 10, 10);
		}
	}

	@Override
	protected void setWidth(double value)
	{
		super.setWidth(value);
	}

	@Override
	protected void setHeight(double value)
	{
		super.setHeight(value);
	}

}

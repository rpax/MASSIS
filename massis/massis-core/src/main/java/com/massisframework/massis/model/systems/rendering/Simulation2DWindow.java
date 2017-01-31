package com.massisframework.massis.model.systems.rendering;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class Simulation2DWindow {

	@FXML
	private AnchorPane mainAnchorPane;
	@FXML
	private Canvas canvas;

	@FXML
	public void initialize()
	{
		configureCanvasPane();
		drawShapes(canvas.getGraphicsContext2D());
	}

	private void configureCanvasPane()
	{

		
		updateCanvasSize();
		// AnchorPane.setLeftAnchor(canvasPane, 0D);
		// AnchorPane.setRightAnchor(canvasPane, 0D);
		// AnchorPane.setTopAnchor(canvasPane, 0D);
		// AnchorPane.setBottomAnchor(canvasPane, 0D);

		mainAnchorPane.widthProperty().addListener((obs, o, n) -> {
			System.out.println(mainAnchorPane.getWidth());
			updateCanvasSize();
		});
		mainAnchorPane.heightProperty().addListener((obs, o, n) -> {
			updateCanvasSize();
		});

	}

	private void updateCanvasSize()
	{
		canvas.setWidth(mainAnchorPane.getWidth());
		canvas.setHeight(mainAnchorPane.getHeight());
		drawShapes(canvas.getGraphicsContext2D());
	}

	private void drawShapes(GraphicsContext gc)
	{
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, 10000, 100000);
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
	}

}

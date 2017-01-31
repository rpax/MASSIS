package com.massisframework.massis.model.systems.rendering;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class Simulation2DWindow {

	@FXML
	private AnchorPane mainAnchorPane;
	private Canvas canvas;
	private double scale;
	private double translateX;
	private double translateY;

	@FXML
	public void initialize()
	{
		configureCanvasPane();
	}

	private void configureCanvasPane()
	{

		this.canvas = new ResizableCanvas();
		this.mainAnchorPane.getChildren().add(this.canvas);
		updateCanvasSize();
		AnchorPane.setLeftAnchor(canvas, 0D);
		AnchorPane.setRightAnchor(canvas, 0D);
		AnchorPane.setTopAnchor(canvas, 0D);
		AnchorPane.setBottomAnchor(canvas, 0D);

		mainAnchorPane.prefHeightProperty().addListener((obs, o, n) -> {
			System.out.println("Pref h changed");
		});
		mainAnchorPane.widthProperty().addListener((obs, o, n) -> {

			updateCanvasSize();
		});
		mainAnchorPane.heightProperty().addListener((obs, o, n) -> {

			updateCanvasSize();
		});

	}

	public void refresh()
	{
		// canvas.autosize();
	}

	private void updateCanvasSize()
	{
		// canvas.resize(mainAnchorPane.getWidth(),mainAnchorPane.getHeight());
		canvas.setWidth(mainAnchorPane.getWidth());
		canvas.setHeight(mainAnchorPane.getHeight());
		// canvas.autosize();
		// refresh();
		// drawShapes(canvas.getGraphicsContext2D());
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

	public Canvas getCanvas()
	{
		return this.canvas;
	}

	private static class ResizableCanvas extends Canvas {

		double _tmp_width = 64;
		double _tmp_heigth = 64;

		public ResizableCanvas()
		{
			new AnimationTimer() {

				@Override
				public void handle(long now)
				{
					if (ResizableCanvas.this.getWidth() != _tmp_width)
					{
						ResizableCanvas.this.setWidth(_tmp_width);
					}
					if (ResizableCanvas.this.getHeight() != _tmp_heigth)
					{
						ResizableCanvas.this.setHeight(_tmp_heigth);
					}
				}
			}.start();
		}

		@Override
		public double minHeight(double width)
		{
			return 64;
		}

		@Override
		public double maxHeight(double width)
		{
			return 1000;
		}

		@Override
		public double prefHeight(double width)
		{
			return minHeight(width);
		}

		@Override
		public double minWidth(double height)
		{
			return 0;
		}

		@Override
		public double maxWidth(double height)
		{
			return 10000;
		}

		@Override
		public boolean isResizable()
		{
			return true;
		}

		@Override
		public void resize(double width, double height)
		{
//			super.setWidth(width);
//			super.setHeight(height);
			_tmp_width = width;
			_tmp_heigth = height;
			// repaint?
		}
	}

}

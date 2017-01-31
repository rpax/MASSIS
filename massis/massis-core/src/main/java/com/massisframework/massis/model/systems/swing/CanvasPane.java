package com.massisframework.massis.model.systems.swing;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

public class CanvasPane extends Pane {

	private final Canvas canvas;

	public CanvasPane()
	{
		canvas = new Canvas(100, 100);
		getChildren().add(canvas);
	}

	public Canvas getCanvas()
	{
		return canvas;
	}

	@Override
	protected void layoutChildren()
	{
		final double x = snappedLeftInset();
		final double y = snappedTopInset();
		final double w = snapSize(getWidth()) - x - snappedRightInset();
		final double h = snapSize(getHeight()) - y - snappedBottomInset();
		canvas.setLayoutX(x);
		canvas.setLayoutY(y);
		canvas.setWidth(w);
		canvas.setHeight(h);
	}

	@Override
	public void setWidth(double value)
	{
		super.setWidth(value);
	}

	@Override
	public void setHeight(double value)
	{
		super.setHeight(value);
	}
}

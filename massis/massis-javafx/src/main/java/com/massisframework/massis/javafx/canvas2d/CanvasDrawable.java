package com.massisframework.massis.javafx.canvas2d;

import javafx.scene.canvas.GraphicsContext;

public interface CanvasDrawable {

	double getMaxX();

	double getMaxY();

	double getMinX();

	double getMinY();

	public void draw(GraphicsContext graphics);
}

package com.massisframework.massis.javafx.canvas2d;

import javafx.scene.canvas.GraphicsContext;

public interface CanvasLayer {

	public void draw(GraphicsContext g2d);

	public String getName();

	public boolean isEnabled();

	public void setEnabled(boolean selected);
}

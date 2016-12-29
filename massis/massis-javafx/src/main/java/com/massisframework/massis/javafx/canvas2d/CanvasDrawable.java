package com.massisframework.massis.javafx.canvas2d;

public interface CanvasDrawable<Model> {

	double getMaxX();

	double getMaxY();

	double getMinX();

	double getMinY();
	
	Model getModel();

}

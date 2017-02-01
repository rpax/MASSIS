package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.OLDSimulationEntity;

import javafx.scene.canvas.GraphicsContext;

public interface JFXRenderer {

	public void render(OLDSimulationEntity<?> e, GraphicsContext gc);
}

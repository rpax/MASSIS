package com.massisframework.massis.model.components;

import com.massisframework.massis.sim.ecs.zayes.SimulationEntity;

import javafx.scene.canvas.GraphicsContext;

public interface JFXRenderer {

	public void render(SimulationEntity e, GraphicsContext gc);
}

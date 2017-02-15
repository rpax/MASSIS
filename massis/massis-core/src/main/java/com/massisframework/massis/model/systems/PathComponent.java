package com.massisframework.massis.model.systems;

import java.util.List;

import com.massisframework.massis.sim.ecs.SimulationComponent;
import com.massisframework.massis.util.geom.CoordinateHolder;

public interface PathComponent extends SimulationComponent {

	List<CoordinateHolder> getPath();

}
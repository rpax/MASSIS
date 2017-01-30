package com.massisframework.massis.model.managers.movement.steering;

import com.massisframework.massis.model.managers.movement.Path;
import com.massisframework.massis.sim.ecs.SimulationComponent;

public interface PathComponent extends SimulationComponent {

	public Path getPath();
}

package com.massisframework.massis.model.agents;

import java.util.Map;

import com.massisframework.massis.model.building.SimulationObject;
import com.massisframework.massis.model.location.SimLocation;
import com.massisframework.massis.model.managers.AnimationManager;
import com.massisframework.massis.model.managers.EnvironmentManager;
import com.massisframework.massis.model.managers.movement.MovementManager;
import com.massisframework.massis.model.managers.pathfinding.PathFindingManager;

/**
 * Represents a POI (Point-Of-Interest) in the map
 *
 * @author rpax
 *
 */
public class NamedLocation extends SimulationObject {

	public NamedLocation(final Map<String, String> metadata,
			SimLocation location, MovementManager movementManager,
			AnimationManager animationManager, EnvironmentManager environment,
			PathFindingManager pathManager) {
		super(metadata, location, movementManager, animationManager,
				environment, pathManager);
	}

}

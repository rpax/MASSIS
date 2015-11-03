package com.massisframework.massis.model.agents;

import java.util.Map;
import com.massisframework.massis.model.building.SimulationObject;
import com.massisframework.massis.model.location.SimLocation;
import com.massisframework.massis.model.managers.AnimationManager;
import com.massisframework.massis.model.managers.EnvironmentManager;
import com.massisframework.massis.model.managers.movement.MovementManager;

/**
 * Represents a POI in the map
 *
 * @author rpax
 *
 */
public class NamedLocation extends SimulationObject {

    private static final long serialVersionUID = 1L;

    public NamedLocation(final Map<String, String> metadata,
            SimLocation location, MovementManager movementManager,
            AnimationManager animationManager, EnvironmentManager environment)
    {
        super(metadata, location, movementManager, animationManager, environment);
    }

    
}

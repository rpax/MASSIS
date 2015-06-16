package rpax.massis.model.building;

import java.util.Map;

import rpax.massis.model.location.SimLocation;
import rpax.massis.model.managers.AnimationManager;
import rpax.massis.model.managers.EnvironmentManager;
import rpax.massis.model.managers.movement.MovementManager;
/**
 * Represents a Window
 * @author rpax
 *
 */
public class SimWindow extends SimulationObject {

	private static final long serialVersionUID = 1L;

	public SimWindow(Map<String, String> metadata, SimLocation location,
			MovementManager movementManager, AnimationManager animationManager,
			EnvironmentManager environment, String resourcesFolder) {
		super(metadata, location, movementManager, animationManager, environment,
				resourcesFolder);
	}

}

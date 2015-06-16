/**
 * 
 */
package rpax.massis.model.building;

import java.util.Map;

import rpax.massis.model.location.SimLocation;
import rpax.massis.model.managers.AnimationManager;
import rpax.massis.model.managers.EnvironmentManager;
import rpax.massis.model.managers.movement.MovementManager;
import rpax.massis.util.io.JsonState;

/**
 * Represents a Wall.
 * @author rpax
 * 
 */
public class SimWall extends SimulationObject {

	private static final long serialVersionUID = 1L;

	

	public SimWall(Map<String, String> metadata, SimLocation location,
			MovementManager movementManager, AnimationManager animationManager,
			EnvironmentManager environment,String resourcesFolder) {
		super(metadata, location, movementManager, animationManager, environment,resourcesFolder);
	}



	@Override
	public JsonState getState() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}

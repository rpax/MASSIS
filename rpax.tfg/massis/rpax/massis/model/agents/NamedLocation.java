package rpax.massis.model.agents;

import java.util.Map;

import rpax.massis.model.building.SimulationObject;
import rpax.massis.model.location.SimLocation;
import rpax.massis.model.managers.AnimationManager;
import rpax.massis.model.managers.EnvironmentManager;
import rpax.massis.model.managers.movement.MovementManager;
/**
 * Represents a POI in the map
 * @author rpax
 *
 */
public class NamedLocation extends SimulationObject{

	private static final long serialVersionUID = 1L;

	

	public NamedLocation(Map<String, String> metadata, SimLocation location,
			MovementManager movementManager, AnimationManager animationManager,
			EnvironmentManager environment,String resourcesFolder) {
		super(metadata, location, movementManager, animationManager, environment,resourcesFolder);
	}



	@Override 
	public void step() {}

}

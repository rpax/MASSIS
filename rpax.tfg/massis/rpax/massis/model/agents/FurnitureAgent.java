package rpax.massis.model.agents;

import java.util.Map;

import rpax.massis.ia.AIController;
import rpax.massis.ia.DummyAIController;
import rpax.massis.model.location.SimLocation;
import rpax.massis.model.managers.AnimationManager;
import rpax.massis.model.managers.EnvironmentManager;
import rpax.massis.model.managers.movement.MovementManager;
/**
 * Agent representing furniture. Does nothing
 * @author rpax
 *
 */
public class FurnitureAgent extends Agent {


	private static final long serialVersionUID = 1L;

	public FurnitureAgent(Map<String, String> metadata, SimLocation location,
			MovementManager movementManager, AnimationManager animationManager,
			EnvironmentManager environment,String resourcesFolder) {
		super(metadata, location, movementManager, animationManager,
				environment,resourcesFolder);
	}

	@Override
	protected AIController<? extends Agent> createIAController() {
		return new DummyAIController<Agent>(this);
	}

}

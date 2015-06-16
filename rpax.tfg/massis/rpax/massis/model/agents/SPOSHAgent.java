package rpax.massis.model.agents;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import rpax.massis.ia.AIController;
import rpax.massis.ia.sposh.SPOSHLogicController;
import rpax.massis.model.location.SimLocation;
import rpax.massis.model.managers.AnimationManager;
import rpax.massis.model.managers.EnvironmentManager;
import rpax.massis.model.managers.movement.MovementManager;

/**
 * Agent which AI mmodule is driven by a SPOSH Engine
 * @author rpax
 */
public class SPOSHAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String planFile;

	public SPOSHAgent(
			Map<String, String> metadata,
			SimLocation location,
			MovementManager movementManager,
			AnimationManager animationManager,
			EnvironmentManager environment,
			String resourcesFolder) {
		
		super(metadata, location, movementManager, animationManager,
				environment,resourcesFolder);
		Path path = Paths.get(this.resourcesFolder,metadata.get(PLAN_FILE_PATH));
		this.planFile = path.toString();
	}

	@Override
	protected AIController<? extends SPOSHAgent> createIAController() {
		return new SPOSHLogicController<>(this, this.planFile);
	}

}

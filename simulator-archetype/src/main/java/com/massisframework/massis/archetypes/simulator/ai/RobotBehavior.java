package com.massisframework.massis.archetypes.simulator.ai;

import java.util.Map;

import com.massisframework.massis.model.agents.HighLevelController;
import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.location.Location;

public class RobotBehavior extends HighLevelController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Location currentTarget;

	public RobotBehavior(LowLevelAgent agent, Map<String, String> metadata, String resourcesFolder) {
		super(agent, metadata, resourcesFolder);
		// com.massisframework.massis.archetypes.simulator.ai.RobotBehavior
	}

	@Override
	public void stop() {

	}

	@Override
	public void step() {
		if (this.currentTarget == null) {
			this.currentTarget = this.agent.approachToRandomTarget();
		} else {
			while (!this.currentTarget.isInSameFloor(this.agent.getLocation()) || this.agent.approachTo(this.currentTarget)) {
				this.currentTarget = this.agent.approachToRandomTarget();
			}
		}
	}

}

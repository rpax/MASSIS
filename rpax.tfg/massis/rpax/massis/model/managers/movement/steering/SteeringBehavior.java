package rpax.massis.model.managers.movement.steering;

import rpax.massis.model.agents.Agent;
import rpax.massis.util.geom.KVector;

public abstract class SteeringBehavior {
	protected Agent v;
	public SteeringBehavior(Agent v) {
		this.v=v;
	}
	public abstract KVector steer();
}

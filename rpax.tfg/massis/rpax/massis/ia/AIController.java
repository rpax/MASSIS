package rpax.massis.ia;

import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.agents.Agent;
/**
 * AI Controller of an agent
 * @author rpax
 *
 * @param <A> the agent type
 */
public abstract class AIController<A extends Agent> {

	protected A agent;

	public AIController(A agent) {
		this.agent = agent;
	}
	public abstract SimulationContext<A> getContext();
	public abstract void logic();
}

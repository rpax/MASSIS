package rpax.massis.ia;

import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.agents.Agent;
/**
 * AI controller that does nothing.
 * @author rpax
 *
 * @param <A> the agent type.
 */
public class DummyAIController<A extends Agent> extends AIController<A> {

	public DummyAIController(A agent) {
		super(agent);
	}

	@Override
	public void logic() {
		//nothing
	}

	@Override
	public SimulationContext<A> getContext() {
		throw new UnsupportedOperationException("DummyAIController has not any context.");
	}

}

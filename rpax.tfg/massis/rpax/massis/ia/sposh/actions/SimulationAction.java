/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ia.sposh.actions;

import rpax.massis.ia.sposh.AgentHolder;
import rpax.massis.ia.sposh.MentalStateKeys;
import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.agents.SPOSHAgent;
import cz.cuni.amis.pogamut.sposh.executor.ParamsAction;

/**
 * Represents an SPOSH action in MASSIS simulation engine
 * 
 * @author rpax
 */
public abstract class SimulationAction<SO extends SPOSHAgent> extends
		ParamsAction<SimulationContext<SO>> implements AgentHolder<SO>,
		MentalStateKeys {

	public SimulationAction(SimulationContext<SO> ctx) {
		super(ctx);
	}

	/*
	 * The SPOSH engine calls init() and done() with
	 * reflection. Making these two methods abstract, forces the underlaying
	 * classes to implement it, avoiding errors.
	 */
	public abstract void init();

	public abstract void done();

	@Override
	public SO getAgent() {
		return this.ctx.getBot();
	}
}

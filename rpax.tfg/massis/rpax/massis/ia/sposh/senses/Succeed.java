/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ia.sposh.senses;

import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.agents.SPOSHAgent;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

/**
 * Primitive sense, always succeed (return true) and do nothing else.
 * 
 * @author rpax
 */
@PrimitiveInfo(name = "Succeed", description = "Returns always true")
public class Succeed extends SimulationSense<SPOSHAgent, Boolean> {

	public Succeed(SimulationContext<SPOSHAgent> ctx) {
		super(ctx);
	}

	public Boolean query() {
		return true;
	}
}

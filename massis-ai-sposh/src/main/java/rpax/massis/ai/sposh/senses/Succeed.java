/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ai.sposh.senses;

import rpax.massis.ai.sposh.SimulationContext;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

/**
 * Primitive sense, always succeed (return true) and do nothing else.
 * 
 * @author rpax
 */
@PrimitiveInfo(name = "Succeed", description = "Returns always true")
public class Succeed extends SimulationSense<Boolean> {

	public Succeed(SimulationContext ctx) {
		super(ctx);
	}

	public Boolean query() {
		return true;
	}
}

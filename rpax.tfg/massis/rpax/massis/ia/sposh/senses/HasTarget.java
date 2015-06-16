/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ia.sposh.senses;

import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.agents.SPOSHAgent;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

/**
 * Returns if the agent has a target assigned
 *
 * @author rpax
 */
@PrimitiveInfo(name = "Has Target", description = "Return if the agent has a target assigned")
public class HasTarget<SO extends SPOSHAgent> extends SimulationSense<SO, Boolean> {

    public HasTarget(SimulationContext<SO> ctx) {
        super(ctx);
    }

   
    public Boolean query() {
        return this.ctx.getTarget()!=null;
    }
}

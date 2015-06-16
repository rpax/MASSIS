/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ia.sposh.actions;

import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;
import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.agents.SPOSHAgent;

/**
 * Primitive action that doesn't do anything. It is used in empty plans and so on.
 * @author rpax
 */
@PrimitiveInfo(name = "Nothing", description = "This action does nothing and lasts one iteration.")
public class DoNothing extends SimulationAction<SPOSHAgent> {

    public DoNothing(SimulationContext<SPOSHAgent> ctx) {
        super(ctx);
    }

    
    public ActionResult run() {
        return ActionResult.RUNNING_ONCE;
    }

    public void init() {}
    public void done() {}
}

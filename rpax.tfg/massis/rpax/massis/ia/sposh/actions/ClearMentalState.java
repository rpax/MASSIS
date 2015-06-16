/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ia.sposh.actions;

import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.agents.SPOSHAgent;
import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
import cz.cuni.amis.pogamut.sposh.executor.Param;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

/**
 * Clears a mental state of the agent
 * @author rpax
 */
@PrimitiveInfo(name = "Clear Mental State", description = "Clears a mental state of the agent",tags={"parallel"})
public class ClearMentalState extends SimulationAction<SPOSHAgent> {

    public ClearMentalState(SimulationContext<SPOSHAgent> ctx) {
        super(ctx);
    }

    
    public ActionResult run(@Param("$mentalVariable") String mentalVariable) {
        this.ctx.getMentalState().remove(mentalVariable);
        return ActionResult.FINISHED;
    }

    @Override
	public void init() {}
    @Override
	public void done() {}
}

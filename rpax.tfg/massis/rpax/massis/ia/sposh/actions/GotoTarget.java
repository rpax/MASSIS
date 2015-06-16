/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ia.sposh.actions;

import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.agents.SPOSHAgent;
import rpax.massis.model.location.Location;
import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

/**
 * Goes to assigned target
 * @author rpax
 */
@PrimitiveInfo(name = "Go to target", description = "Goes to assigned target", tags = { "parallel" })
public class GotoTarget extends SimulationAction<SPOSHAgent> {

	public GotoTarget(SimulationContext<SPOSHAgent> ctx) {
		super(ctx);
	}

	@Override
	public void done() {
	}

	public ActionResult run() {
		Location target = this.ctx.getTarget();
		boolean isInLoc = this.getAgent().approachTo(target);
		if (isInLoc)
		{
			return ActionResult.RUNNING_ONCE;
		}
		else
		{
			return ActionResult.FINISHED;
		}
	}

	@Override
	public void init() {
	}
}

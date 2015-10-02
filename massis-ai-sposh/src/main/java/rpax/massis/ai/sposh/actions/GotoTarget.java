/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ai.sposh.actions;

import rpax.massis.ai.sposh.SimulationContext;
import rpax.massis.model.location.Location;
import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;
import rpax.massis.model.agents.LowLevelAgent;

/**
 * Goes to assigned target
 * @author rpax
 */
@PrimitiveInfo(name = "Go to target", description = "Goes to assigned target", tags = { "parallel" })
public class GotoTarget extends SimulationAction {

	public GotoTarget(SimulationContext ctx) {
		super(ctx);
	}

	@Override
	public void done() {
	}

	public ActionResult run() {
		Location target = this.ctx.getTarget();
                LowLevelAgent agent=this.ctx.getBot();
		boolean isInLoc = agent.approachTo(target);
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

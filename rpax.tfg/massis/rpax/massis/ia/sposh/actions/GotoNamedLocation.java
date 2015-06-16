/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ia.sposh.actions;

import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.agents.SPOSHAgent;
import rpax.massis.model.location.Location;
import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
import cz.cuni.amis.pogamut.sposh.executor.Param;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

/**
 * Tells the agent to move to an specific location
 * @author rpax
 */
@PrimitiveInfo(name = "Go to location", description = "Tells the agent to move to an specific location",tags={"parallel"})
public class GotoNamedLocation extends SimulationAction<SPOSHAgent> {

	public GotoNamedLocation(SimulationContext<SPOSHAgent> ctx) {
		super(ctx);
	}

	@Override
	public void init() {

	}

	@Override
	public void done() {
	}

	public ActionResult run(@Param("$name") String name) {
		
		Location target = this.ctx.getBot().getEnvironment().getNamedLocation(name);
		boolean isInLoc = this.ctx.getBot().approachTo(target);
		if (isInLoc)
		{
			return ActionResult.RUNNING_ONCE;
		}
		else
		{
			return ActionResult.FINISHED;
		}

	}
}

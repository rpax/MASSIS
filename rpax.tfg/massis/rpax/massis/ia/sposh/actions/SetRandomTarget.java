package rpax.massis.ia.sposh.actions;

import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.agents.SPOSHAgent;
import rpax.massis.model.location.Location;
import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;
/**
 * Sets a random target
 * @author rpax
 *
 */
@PrimitiveInfo(name = "Set Random Target", description = "Sets a random target", tags = { "parallel" })
public class SetRandomTarget extends SimulationAction<SPOSHAgent> {

	public SetRandomTarget(SimulationContext<SPOSHAgent> ctx) {
		super(ctx);
	}

	public ActionResult run() {
		Location rndTarget = this.getAgent().getEnvironment().getRandomRoom()
				.getRandomLoc();
		this.ctx.setTarget(rndTarget);
		return ActionResult.RUNNING_ONCE;
	}

	@Override
	public void init() {
		
	}
	@Override
	public void done() {
	}

}

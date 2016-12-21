/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.massis.ai.sposh.actions;

import com.massisframework.massis.ai.sposh.SimulationContext;
import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.managers.movement.ApproachCallback;
import com.massisframework.massis.pathfinding.straightedge.FindPathResult.PathFinderErrorReason;

import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
import cz.cuni.amis.pogamut.sposh.executor.Param;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

/**
 * Tells the agent to move to an specific location
 *
 * @author rpax
 */
@PrimitiveInfo(name = "Go to location", description = "Tells the agent to move to an specific location", tags = {
		"parallel" })
public class GotoNamedLocation extends SimulationAction {

	public GotoNamedLocation(SimulationContext ctx) {
		super(ctx);
	}

	@Override
	public void init() {
	}

	@Override
	public void done() {
	}

	public ActionResult run(@Param("$name") String name) {
		final ActionResult[] res = new ActionResult[1];
		ApproachCallback approachCallBack = new ApproachCallback() {

			@Override
			public void onTargetReached(LowLevelAgent agent) {
				res[0] = ActionResult.FINISHED;
			}

			@Override
			public void onSucess(LowLevelAgent agent) {
				res[0] = ActionResult.RUNNING_ONCE;
			}

			@Override
			public void onPathFinderError(PathFinderErrorReason reason) {
				res[0] = ActionResult.FAILED;
				ctx.setTarget(null);
			}
			
		};
		this.ctx.getBot().approachToNamedLocation(name, approachCallBack);
		return res[0];

	}
}

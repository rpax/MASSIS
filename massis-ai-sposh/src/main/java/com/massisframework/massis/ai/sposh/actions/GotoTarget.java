/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.massis.ai.sposh.actions;

import com.massisframework.massis.ai.sposh.SimulationContext;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.managers.movement.ApproachCallback;
import com.massisframework.massis.pathfinding.straightedge.FindPathResult.PathFinderErrorReason;

import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;
import com.massisframework.massis.model.agents.LowLevelAgent;

/**
 * Goes to assigned target
 * 
 * @author rpax
 */
@PrimitiveInfo(name = "Go to target", description = "Goes to assigned target", tags = {
		"parallel" })
public class GotoTarget extends SimulationAction {

	public GotoTarget(SimulationContext ctx) {
		super(ctx);
	}

	@Override
	public void done() {
	}

	public ActionResult run() {

		Location target = this.ctx.getTarget();
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
				res[0] = ActionResult.FINISHED;
				ctx.setTarget(null);
			}

		};

		this.ctx.getBot().approachTo(target, approachCallBack);

		return res[0];

	}

	@Override
	public void init() {
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.massisframework.massis.ai.sposh.senses;

import com.massisframework.massis.ai.sposh.SimulationContext;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

/**
 * Returns if the agent is near to its target,
 * 
 * @author rpax
 */
@PrimitiveInfo(name = "Near to target", description = "Returns if the agent is near to its target")
public class NearTarget extends
		SimulationSense<Boolean> {

	public NearTarget(SimulationContext ctx) {
		super(ctx);
	}

	public Boolean query() {
		return this.ctx.getTarget() != null
				&& this.ctx.getTarget().distance2D(
						this.ctx.getBot().getLocation()) < this.ctx.getBot()
						.getMaxSpeed()
						+ this.ctx.getBot().getBodyRadius();
	}
}

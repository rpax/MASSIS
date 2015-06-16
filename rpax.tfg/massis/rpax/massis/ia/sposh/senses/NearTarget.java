/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ia.sposh.senses;

import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.agents.SPOSHAgent;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

/**
 * Returns if the agent is near to its target,
 * 
 * @author rpax
 */
@PrimitiveInfo(name = "Near to target", description = "Returns if the agent is near to its target")
public class NearTarget<SO extends SPOSHAgent> extends
		SimulationSense<SO, Boolean> {

	public NearTarget(SimulationContext<SO> ctx) {
		super(ctx);
	}

	public Boolean query() {
		return this.ctx.getTarget() != null
				&& this.ctx.getTarget().distance2D(
						this.ctx.getBot().getLocation()) < this.ctx.getBot()
						.getMaxSpeed()
						+ this.getAgent().getPolygon().getRadius();
	}
}

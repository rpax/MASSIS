/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ia.sposh.senses;

import java.util.Map;

import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.agents.SPOSHAgent;
import cz.cuni.amis.pogamut.sposh.executor.Param;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

/**
 * Returns if the specified mental state contains the provided value
 * 
 * @author rpax
 */
@PrimitiveInfo(name = "Mental State Contains", description = "Returns if the specified mental state contains the provided value")
public class MentalStateContains<SO extends SPOSHAgent> extends
		SimulationSense<SO, Boolean> {

	public MentalStateContains(SimulationContext<SO> ctx) {
		super(ctx);
	}

	public Boolean query(@Param("$mentalVariable") String message,
			@Param("$value") Integer from) {

		@SuppressWarnings("unchecked")
		SPOSHAgent sender = ((Map<String, SPOSHAgent>) this.ctx.getMentalState().get(MESSAGES)).remove(message);
		if (sender == null)
		{
			return false;
		}
		else
		{
			this.ctx.getMentalState().put(SENDER, sender);
			return true;
		}

	}
}

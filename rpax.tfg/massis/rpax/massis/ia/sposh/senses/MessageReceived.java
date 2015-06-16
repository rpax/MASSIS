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
 * Returns if a specific message was received
 * 
 * @author rpax
 */
@PrimitiveInfo(name = "Message Received", description = "Returns if a specific message was received")
public class MessageReceived<SO extends SPOSHAgent> extends
		SimulationSense<SO, Boolean> {

	public MessageReceived(SimulationContext<SO> ctx) {
		super(ctx);
	}

	public Boolean query(@Param("$message") String message,
			@Param("$from") String from) {

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

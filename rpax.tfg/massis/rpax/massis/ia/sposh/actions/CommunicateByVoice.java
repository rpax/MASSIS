/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ia.sposh.actions;

import java.util.HashMap;
import java.util.Map;

import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.agents.Agent;
import rpax.massis.model.agents.SPOSHAgent;
import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
import cz.cuni.amis.pogamut.sposh.executor.Param;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

/**
 * Sends a message to every agent in the vision area
 * @author rpax
 */
@PrimitiveInfo(name = "Communicate By Voice", description = "Sends a message in an area")
public class CommunicateByVoice extends SimulationAction<SPOSHAgent> {

	public CommunicateByVoice(SimulationContext<SPOSHAgent> ctx) {
		super(ctx);
	}

	@Override
	public void init() {
	}

	@Override
	public void done() {
	}

	@SuppressWarnings("unchecked")
	public ActionResult run(@Param("$message") String message) {

		for (Agent agent : this.getAgent().getAgentsInVisionRadio())
		{
			if (agent instanceof SPOSHAgent)
			{
				HashMap<String, Object> agentMentalState = ((SPOSHAgent) agent).getContext().getMentalState();
				if (!agentMentalState.containsKey(MESSAGES))
				{
					agentMentalState.put(MESSAGES,
							new HashMap<String, SPOSHAgent>());
				}
				((Map<String, SPOSHAgent>) agentMentalState.get(MESSAGES))
						.put(message, this.getAgent());
			}
		}
		return ActionResult.FINISHED;
	}

}

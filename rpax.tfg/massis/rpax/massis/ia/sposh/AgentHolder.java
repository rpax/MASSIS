/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ia.sposh;

import rpax.massis.model.building.SimulationObject;

/**
 * Contains an agent.
 * @author rpax
 */
public interface AgentHolder<SO extends SimulationObject> {
	/**
	 * 
	 * @return the agent that this holder contains
	 */
    public SO getAgent();
}
